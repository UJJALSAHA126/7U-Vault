package com.example.a7uvault

class Constants {
    companion object{
        const val FRONT_IMAGE = 77
        const val BACK_IMAGE = 88
        const val CAMERA_REQUEST_CODE0 = 100
        const val CAMERA_REQUEST_CODE1 = 101
    }
}

//private fun blurBackground(){
//    val radius = 20F
//    val decorView = requireActivity().window.decorView
//    val rootView = decorView.findViewById<ViewGroup>(R.id.content)
//    val windowBackground = decorView.background
//    binding.blurView1.setupWith(rootView)
//        .setFrameClearDrawable(windowBackground)
//        .setBlurAlgorithm(RenderScriptBlur(context))
//        .setBlurRadius(radius)
//        .setHasFixedTransformationMatrix(true)
//}