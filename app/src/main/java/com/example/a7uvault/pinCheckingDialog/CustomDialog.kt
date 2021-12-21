package com.example.a7uvault.pinCheckingDialog

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.a7uvault.databinding.PinCheckingDialogBinding
import com.example.a7uvault.pinCheckingDialog.SharedPin.getPinFromShared

class CustomDialog(
    private val activity: Activity,
    private val context: Context,
    private val parent: ViewGroup?,
    private val blurView: View,
    private val stopped:() -> Unit
) {

    private var dialog: Dialog? = null
    private var count = 0
    private val pinShared = getPinFromShared(activity)

    fun startLoading() {
        count = 0
        blurView.alpha = 1F
        dialog = Dialog(context)
        val binding: PinCheckingDialogBinding =
            PinCheckingDialogBinding.inflate(LayoutInflater.from(context), parent, false)

        dialog?.apply {
            setContentView(binding.root)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCancelable(false)
            show()
        }

        val pinText = binding.enteredConfirmPin

        binding.confirmBtn.setOnClickListener {
            val pin = pinText.text.toString()
            if (pin.trim().isEmpty()) {
                Toast.makeText(context, "Please Enter A Pin.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            count++
            if (pin == pinShared) {
                stopLoading()
                stopped()
            } else {
                if (count > 5) {
                    stopLoading()
                    blurView.alpha = 1F
                    activity.finishAffinity()
                } else {
                    pinText.text.clear()
                    pinText.error = "Invalid Pin"
                    pinText.hint = "Wrong Pin"
                }
            }
        }

        binding.cancelBtn.setOnClickListener {
            stopLoading()
            blurView.alpha = 1F
            activity.finishAffinity()
        }
    }

    fun stopLoading() {
        blurView.alpha = 0F
        dialog?.dismiss()
    }
}