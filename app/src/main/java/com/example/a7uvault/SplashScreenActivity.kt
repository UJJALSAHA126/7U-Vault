package com.example.a7uvault

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.a7uvault.databinding.ActivitySplashScreenBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var shared: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private var pin = ""
    private var bool: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        shared = getSharedPreferences("UserPin", Context.MODE_PRIVATE)
        pin = shared.getString("pin", "").toString()
        bool = (pin == "")

        binding.inputLay.hint = if (bool) {
            "Create A Pin"
        } else {
            "Enter Your Pin"
        }

        binding.verifyBtn.setOnClickListener {
            val x = binding.userPin.text.toString().trim()
            when {
                x.isEmpty() -> {
                    Toast.makeText(this, "Please Enter A Valid Pin.", Toast.LENGTH_SHORT).show()
                }
                x.length < 4 -> {
                    Toast.makeText(this, "Pin Must Be Greater Than 4 digits.", Toast.LENGTH_SHORT)
                        .show()
                }
                else -> {
                    setUpPin(x)
                }
            }
        }
    }

    private fun setUpPin(x: String) {
        if (bool) {
            editor = shared.edit()
            editor.putString("pin", x)
            editor.apply()
            changeActivity()
        } else {
            if (x == pin) {
                changeActivity()
            } else {
                Toast.makeText(this, "Invalid Pin...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun changeActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}