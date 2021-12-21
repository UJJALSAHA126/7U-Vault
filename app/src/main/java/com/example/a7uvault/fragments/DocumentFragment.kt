package com.example.a7uvault.fragments

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.example.a7uvault.adapter.SliderAdapter
import com.example.a7uvault.databinding.FragmentDocumentBinding
import com.example.a7uvault.model.InternalStorageDocument
import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class DocumentFragment : Fragment() {

    private var _binding: FragmentDocumentBinding? = null
    private val binding get() = _binding!!

    private val args: DocumentFragmentArgs by navArgs()
    private lateinit var docArray: Array<String>
    private var imgFront: Bitmap? = null
    private var imgBack: Bitmap? = null
    private var name = ""
    private var docType = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDocumentBinding.inflate(inflater, container, false)

        docArray = args.userDocument

        name = docArray[0]
        docType = docArray[1]

        try {
            lifecycleScope.launch {
                delay(500)
                withContext(Dispatchers.Main) {
                    requireActivity().toolbar.title = "$name / $docType"
                }
            }
        } catch (e: Exception) {
        }

//        if (docArray[2].isNotEmpty() || docArray[3].isNotEmpty()) {
        lifecycleScope.launch(Dispatchers.IO) {
            var allDocuments = loadAllDocumentsFromInternalStorage()
            if (allDocuments.isNotEmpty()) {
//                Log.d("ABCDE", allDocuments[0].name)
//                Log.d("ABCDE", allDocuments[1].name)
                allDocuments = allDocuments.sortedBy { it.name }
//                Log.d("ABCDE", allDocuments[0].name)
//                Log.d("ABCDE", allDocuments[1].name)

                withContext(Dispatchers.Main) {
                    val images = ArrayList<Bitmap>()
                    try {
                        imgFront = allDocuments[0].bitmap
                        imgBack = allDocuments[1].bitmap
                    } catch (e: Exception) {
                    }
                    imgFront?.let {
                        images.add(it)
                    }
                    imgBack?.let {
                        images.add(it)
                    }
                    val adapter = SliderAdapter(images)
                    binding.sliderView.apply {
                        setSliderAdapter(adapter)
                        setIndicatorAnimation(IndicatorAnimationType.WORM)
                        setSliderTransformAnimation(SliderAnimations.DEPTHTRANSFORMATION)
                    }
                }
            }
        }
//        }

        return binding.root
    }

    private suspend fun loadAllDocumentsFromInternalStorage(): List<InternalStorageDocument> {
        return withContext(Dispatchers.IO) {
            val files = requireActivity().filesDir.listFiles()
            files?.filter { it.canRead() && it.isFile && checkCondition(it) }?.map {
                val bytes = it.readBytes()
                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
//                Toast.makeText(context, "Found", Toast.LENGTH_SHORT).show()
                InternalStorageDocument(it.name, bmp)
            } ?: listOf()
        }
    }

    private fun checkCondition(file: File): Boolean {
        return (file.name == "${docArray[2]}.jpg" || file.name == "${docArray[3]}.jpg")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}