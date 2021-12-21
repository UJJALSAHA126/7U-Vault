package com.example.a7uvault.fragments

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.a7uvault.MainActivity
import com.example.a7uvault.R
import com.example.a7uvault.database.UserDocument
import com.example.a7uvault.databinding.CustomDialogLayoutBinding
import com.example.a7uvault.databinding.FragmentAddDocumentBinding
import com.example.a7uvault.viewModel.DocumentViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

class AddDocumentFragment : Fragment() {

    private var _binding: FragmentAddDocumentBinding? = null
    private val binding get() = _binding!!

    private var imgFront: Bitmap? = null
    private var imgBack: Bitmap? = null
    private lateinit var docViewModel: DocumentViewModel
    private lateinit var cameraIntent: Intent
    private lateinit var customDialog: Dialog

    private lateinit var imageForFront: ActivityResultLauncher<Intent>
    private lateinit var imageForBack: ActivityResultLauncher<Void>

    private lateinit var imageFrontFromGallery: ActivityResultLauncher<String>
    private lateinit var imageBackFromGallery: ActivityResultLauncher<String>

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    private var uriFront = ""
    private var uriBack = ""
    private var name = ""
    private var type = ""
    private var timeLong: Long = 0

    private var code = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAddDocumentBinding.inflate(inflater, container, false)

        requireActivity().toolbar.title = "Add A Document"
        cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        docViewModel = ViewModelProvider(this)[DocumentViewModel::class.java]
        customDialog = Dialog(requireContext())
        customDialog.apply {
            val bnd = CustomDialogLayoutBinding.inflate(
                LayoutInflater.from(requireContext()),
                container,
                false
            )
            setContentView(bnd.root)
            setCancelable(false)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        setHasOptionsMenu(true)

        initialize()

        binding.imgFrontAdd.setOnClickListener {
            try {
                if (binding.cameraToggle.isChecked) {
                    if (hasCameraPermission()) {
                        MainActivity.gettingUri()
                        imageForFront.launch(cameraIntent)
                    } else {
                        code = 1
                        requestCameraPermission()
                    }
                } else {
                    MainActivity.gettingUri()
                    imageFrontFromGallery.launch("image/*")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("@@@@@", e.message.toString())
            }
        }

        binding.imgBackAdd.setOnClickListener {
            try {
                if (binding.cameraToggle.isChecked) {
                    if (hasCameraPermission()) {
                        MainActivity.gettingUri()
                        imageForBack.launch(null)
                    } else {
                        code = 0
                        requestCameraPermission()
                    }
                } else {
                    MainActivity.gettingUri()
                    imageBackFromGallery.launch("image/*")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.d("@@@@@", e.message.toString())
            }
        }

        return binding.root
    }

    private fun initialize() {
        imageForFront =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                it?.let {
                    it.data?.extras?.apply {
                        imgFront = get("data") as Bitmap
                        binding.userDocImgFront.setImageBitmap(imgFront)
                    }
                }
            }

        imageForBack =
            registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
                it?.let {
                    imgBack = it
                    binding.userDocImgBack.setImageBitmap(it)
                }
            }

        imageFrontFromGallery =
            registerForActivityResult(ActivityResultContracts.GetContent()) {
                binding.userDocImgFront.setImageURI(it)
                it?.let {
                    val input = requireContext().contentResolver.openInputStream(it)
                    imgFront = BitmapFactory.decodeStream(input)
                }
            }

        imageBackFromGallery =
            registerForActivityResult(ActivityResultContracts.GetContent()) {
                binding.userDocImgBack.setImageURI(it)
                it?.let {
                    val input = requireContext().contentResolver.openInputStream(it)
                    imgBack = BitmapFactory.decodeStream(input)
                }
            }

        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
//                    Log.d("####", "Granted")
                    if (code == 1) {
                        imageForFront.launch(cameraIntent)
                    } else {
                        imageForBack.launch(null)
                    }
                } else {
//                    Log.d("####", "Denied")
                    binding.cameraToggle.isChecked = false
                    Toast.makeText(context, "Permission Denied..", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun hasCameraPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        Log.d("####", "Requested")
        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.doneAdd) {
            if (checkCondition()) {
                customDialog.show()
                Log.d("@@@@", "Start")
                lifecycleScope.launch(Dispatchers.IO) {
                    addDocument()
                }
            } else {
                Toast.makeText(context, "Please Provide Proper Details.", Toast.LENGTH_SHORT).show()
            }
        }
        return true
    }

    private suspend fun addDocument() {
        timeLong = System.currentTimeMillis()

        name = binding.userNameAdd.text.toString().trim()
        type = binding.userDocTypeAdd.text.toString().trim()
//        try {
//            imgFront = (binding.userDocImgFront.drawable as BitmapDrawable).bitmap
//        } catch (e: Exception) {
//        }
//        try {
//            imgBack = (binding.userDocImgBack.drawable as BitmapDrawable).bitmap
//        } catch (e: Exception) {
//        }

        withContext(Dispatchers.Main) {
            imgFront?.let { frontB ->
                uriFront = timeLong.toString()
                if (saveDocumentToInternalStorage(uriFront, frontB)) {
                    if (imgBack != null) {
                        imgBack?.let { backB ->
                            uriBack = (timeLong + 1).toString()
                            if (saveDocumentToInternalStorage(uriBack, backB)) {
                                done()
                            }
                        }
                    } else {
                        done()
                    }
                }
            }
        }
    }

    private fun done() {
        val doc = UserDocument(
            uriFront, uriBack, null, null, name, type, timeLong
        )
        docViewModel.addDocument(doc)
        Log.d("@@@@", "Finish")
        lifecycleScope.launch(Dispatchers.IO) {
            delay(500)
            withContext(Dispatchers.Main) {
                customDialog.dismiss()
                findNavController().navigateUp()
            }
        }
    }

    private fun saveDocumentToInternalStorage(name: String, bmp: Bitmap): Boolean {
        return try {
            requireActivity().openFileOutput("$name.jpg", Context.MODE_PRIVATE).use { stream ->
                if (!bmp.compress(Bitmap.CompressFormat.JPEG, 90, stream)) {
                    throw IOException("Cannot Save The File")
                } else {
                    Log.d("@@@@@", "Successful Saving Bitmap for $name")
                    Toast.makeText(context, "Successfully Saved..", Toast.LENGTH_SHORT).show()
                }
            }
            true
        } catch (e: IOException) {
            Log.d("@@@@@", "Error Saving Bitmap for $name")
            false
        }
    }

    private fun checkCondition(): Boolean {
        return when {
            (imgFront == null) -> false
            binding.userNameAdd.text.trim().isEmpty() && binding.userDocTypeAdd.text.trim()
                .isEmpty() -> false
            else -> true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.add_fragment_menu, menu)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}