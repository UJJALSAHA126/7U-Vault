package com.example.a7uvault.adapter

import androidx.recyclerview.widget.DiffUtil
import com.example.a7uvault.database.UserDocument

class DocumentDiffUtil(
    private val oldDocs: ArrayList<UserDocument>,
    private val newDocs: ArrayList<UserDocument>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldDocs.size
    }

    override fun getNewListSize(): Int {
        return newDocs.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val old = oldDocs[oldItemPosition]
        val new = newDocs[newItemPosition]
        return old.id == new.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val old = oldDocs[oldItemPosition]
        val new = newDocs[newItemPosition]
        return when {
            old.frontImageUri != new.frontImageUri -> false
            old.backImageUri != new.backImageUri -> false
            old.imgFront != new.imgFront -> false
            old.imgBack != new.imgBack -> false
            old.userName != new.userName -> false
            old.documentType != new.documentType -> false
            else -> true
        }
    }
}