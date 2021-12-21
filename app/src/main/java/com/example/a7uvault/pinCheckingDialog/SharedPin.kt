package com.example.a7uvault.pinCheckingDialog

import android.app.Activity
import android.content.Context

object SharedPin {
    fun getPinFromShared(activity: Activity):String{
        val shared = activity.getSharedPreferences("UserPin", Context.MODE_PRIVATE)
        return shared.getString("pin", "").toString()
    }
}