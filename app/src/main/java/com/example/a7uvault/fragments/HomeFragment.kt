package com.example.a7uvault.fragments

import android.content.Context
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.a7uvault.R
import com.example.a7uvault.adapter.DocumentAdapter
import com.example.a7uvault.database.UserDocument
import com.example.a7uvault.databinding.DeleteBottomSheetBinding
import com.example.a7uvault.databinding.FragmentHomeBinding
import com.example.a7uvault.model.InternalStorageDocument
import com.example.a7uvault.pinCheckingDialog.SharedPin.getPinFromShared
import com.example.a7uvault.viewModel.DocumentViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.android.synthetic.main.delete_bottom_sheet.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: DocumentAdapter
    private lateinit var docViewModel: DocumentViewModel
    private lateinit var searchView: SearchView
    private var pin = ""
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("!!!!!", "onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        Log.d("!!!!!", "onCreateView")
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        hideSoftKeyBoard()

        sharedPreferences = requireActivity().getSharedPreferences("UserPin", Context.MODE_PRIVATE)
//        pin = sharedPreferences.getString("pin", "").toString()
        pin = getPinFromShared(requireActivity())

//        blurBackground()

        setHasOptionsMenu(true)
        adapter = DocumentAdapter(requireContext(), { doc ->
            showDocument(doc)
        }, { doc ->
            deleteDocument(doc)
        })

        docViewModel = ViewModelProvider(this)[DocumentViewModel::class.java]
        docViewModel.allDocuments.observe(requireActivity(), { list ->
            Log.d("@@@@", "Live Data Found " + System.currentTimeMillis() % 10000)
            binding.progressBar0.visibility = View.VISIBLE
            binding.fab.isClickable = false
            val newList = ArrayList<UserDocument>()
            newList.addAll(list)
            Log.d("SORTT", newList.first().userName + "  " + newList.last().userName)
            newList.sortBy { it.frontImageUri }
            Log.d("SORTT", newList.first().userName + "  " + newList.last().userName)
            addDocumentsToAdapter(newList)
        })

        binding.recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = adapter

        binding.fab.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addDocumentFragment)
        }

        return binding.root
    }

    private fun addDocumentsToAdapter(newList: ArrayList<UserDocument>) {

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                var allDocumentsFromStorage = loadAllDocumentsFromInternalStorage()
                Log.d(
                    "SORT",
                    allDocumentsFromStorage.first().name + "  " + allDocumentsFromStorage.last().name
                )

                allDocumentsFromStorage = allDocumentsFromStorage.sortedBy { it.name }
                Log.d(
                    "SORT",
                    allDocumentsFromStorage.first().name + "  " + allDocumentsFromStorage.last().name
                )

                Log.d("@@@@", "${allDocumentsFromStorage.size}  ${newList.size}")
                Log.d("@@@@", "Sorting Start " + System.currentTimeMillis() % 10000)
                newList.forEach { doc ->
                    val fr = doc.frontImageUri + ".jpg"
                    val bc = doc.backImageUri + ".jpg"

                    allDocumentsFromStorage.filter { it.name == fr || it.name == bc }.map {
                        if (it.name == fr)
                            doc.imgFront = it.bitmap
                        if (it.name == bc)
                            doc.imgBack = it.bitmap
                    }

//                allDocumentsFromStorage.forEach { file ->
//                    if (file.name == fr) {
//                        doc.imgFront = file.bitmap
//                    }
//                    if (file.name == bc) {
//                        doc.imgBack = file.bitmap
//                    }
//                    Log.d("@@@@", "Fr = $fr, Bc = $bc,Fn = ${file.name}")
//                }
                }
                Log.d("@@@@", "End " + System.currentTimeMillis() % 10000)
                withContext(Dispatchers.Main) {
                    adapter.setAllDocuments(newList)
                    binding.fab.isClickable = true
                    binding.progressBar0.visibility = View.GONE
                }
            } catch (e: Exception) {
            }
        }
    }

    private fun deleteDocumentFromInternalStorage(name: String) {
        try {
            requireActivity().deleteFile(name)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Delete Failed !", Toast.LENGTH_SHORT).show()
        }
    }

    private suspend fun loadAllDocumentsFromInternalStorage(): List<InternalStorageDocument> {
        return withContext(Dispatchers.IO) {
            val files = requireActivity().filesDir.listFiles()
            files?.filter { it.canRead() && it.isFile && it.name.endsWith(".jpg") }?.map {
                val bytes = it.readBytes()
                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                InternalStorageDocument(it.name, bmp)
            } ?: listOf()
        }
    }

    private fun showDocument(doc: UserDocument) {
        val names = arrayOf(doc.userName, doc.documentType, doc.frontImageUri, doc.backImageUri)
        val action = HomeFragmentDirections.actionHomeFragmentToDocumentFragment(names)
        findNavController().navigate(action)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)

        inflater.inflate(R.menu.home_fragment_menu, menu)
        val search = menu.findItem(R.id.searchDocument)
        searchView = search.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    searchQuery(it)
                }
                hideSoftKeyBoard()
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    searchQuery(it)
                }
                return true
            }
        })
    }

    private fun searchQuery(text: String) {
        val query = "%$text%"
        docViewModel.searchDocument(query).observe(viewLifecycleOwner, { docs ->
            docs?.let {
                val allDoc = ArrayList<UserDocument>()
                allDoc.addAll(it)
                addDocumentsToAdapter(allDoc)
            }
        })
    }

    private fun deleteDocument(doc: UserDocument) {

        val sheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetStyle)
        val dltBinding = DeleteBottomSheetBinding.inflate(layoutInflater, bottomSheet, false)
        sheetDialog.setContentView(dltBinding.root)

        val text = dltBinding.textViewBottomDialog
        val yes = dltBinding.yesButton
        val no = dltBinding.noButton
        "Do You Want\nTo Delete This Document ?".also { text.text = it }

        yes.setOnClickListener {
            docViewModel.deleteDocument(doc)
            deleteDocumentFromInternalStorage(doc.frontImageUri + ".jpg")
            deleteDocumentFromInternalStorage(doc.backImageUri + ".jpg")
            sheetDialog.dismiss()
        }

        no.setOnClickListener {
            sheetDialog.dismiss()
        }

        sheetDialog.show()
    }

    private fun hideSoftKeyBoard() {
        val imm =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }

    override fun onResume() {
        Log.d("!!!!!", "onResume")
        super.onResume()
    }

    override fun onDestroyView() {
        Log.d("!!!!!", "onDestroyView")
        super.onDestroyView()
        _binding = null
    }
}