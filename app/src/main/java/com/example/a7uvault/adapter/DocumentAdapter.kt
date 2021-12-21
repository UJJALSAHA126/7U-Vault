package com.example.a7uvault.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.a7uvault.R
import com.example.a7uvault.database.UserDocument
import com.example.a7uvault.databinding.CustomDocumentLayoutBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DocumentAdapter(
    private val context: Context,
    private val showDocument: (doc: UserDocument) -> Unit,
    private val deleteDoc: (doc: UserDocument) -> Unit
) :
    RecyclerView.Adapter<DocumentAdapter.MyViewHolder>() {

    private val allDocuments = ArrayList<UserDocument>()

    inner class MyViewHolder(binding: CustomDocumentLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val rootLayout = binding.rootLayout
        val imageFront = binding.imgDocument
        val userName = binding.userName
        val documentType = binding.userDocumentType
        val deleteBtn = binding.deleteDocumentBtn
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding =
            CustomDocumentLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val doc = allDocuments[position]
        val animation = AnimationUtils.loadAnimation(context, R.anim.shift_from_top)
        GlobalScope.launch {
            delay(100)
            holder.rootLayout.startAnimation(animation)
        }
        val bm = if (doc.imgFront == null) {
            doc.imgBack
        } else
            doc.imgFront
        holder.imageFront.setImageBitmap(bm)
        holder.userName.text = doc.userName
        holder.documentType.text = doc.documentType
        holder.deleteBtn.setOnClickListener {
            deleteDoc(doc)
        }
        holder.rootLayout.setOnClickListener {
            showDocument(doc)
        }
    }

    override fun getItemCount(): Int {
        return allDocuments.size
    }

    fun setAllDocuments(newAllDocuments: ArrayList<UserDocument>) {
        val diffUtil = DocumentDiffUtil(allDocuments, newAllDocuments)
        val diffResult = DiffUtil.calculateDiff(diffUtil)
        allDocuments.clear()
        allDocuments.addAll(newAllDocuments)
        diffResult.dispatchUpdatesTo(this)
    }
}