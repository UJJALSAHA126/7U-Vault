package com.example.a7uvault

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.a7uvault.databinding.ActivityMainBinding
import com.example.a7uvault.pinCheckingDialog.CustomDialog

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private lateinit var customDialog: CustomDialog
//    val y = Log.d("%%%%", "Static Variable")

    companion object {
        private var canAskPin = false
        private var wasEnabled = false
        fun gettingUri() {
            wasEnabled = false
            canAskPin = false
        }
//        val x = Log.d("%%%%", "Companion Object")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("%%%%", "onCreateActivity")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        canAskPin = wasEnabled
        customDialog = CustomDialog(this, this, null, binding.blurView0) {
            wasEnabled = false
        }

        navController = findNavController(R.id.fragments)
        setSupportActionBar(binding.toolbar)
        binding.toolbar.setupWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.fragments)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }

    override fun onPause() {
        super.onPause()
        Log.d("%%%%", "onPauseActivity")
    }

    override fun onResume() {
        super.onResume()
        customDialog.stopLoading()
        wasEnabled = false
        if (canAskPin) {
            customDialog.startLoading()
            wasEnabled = true

        } else
            canAskPin = true
        Log.d("%%%%", "onResumeActivity")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("%%%%", "onDestroyActivity")
    }
}