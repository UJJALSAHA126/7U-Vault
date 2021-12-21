package com.example.a7uvault.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.a7uvault.databinding.ItemSliderBinding
import com.smarteist.autoimageslider.SliderViewAdapter

class SliderAdapter(private val allImages: ArrayList<Bitmap>) :
    SliderViewAdapter<SliderAdapter.MySliderViewHolder>() {

    inner class MySliderViewHolder(binding: ItemSliderBinding) :
        SliderViewAdapter.ViewHolder(binding.root) {
        val img = binding.imageSlide
    }

    override fun getCount(): Int {
        return allImages.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?): MySliderViewHolder {
        val binding = ItemSliderBinding.inflate(LayoutInflater.from(parent?.context), parent, false)
        return MySliderViewHolder(binding)
    }

    override fun onBindViewHolder(viewHolder: MySliderViewHolder?, position: Int) {
        viewHolder?.img?.setImageBitmap(allImages[position])
    }
}