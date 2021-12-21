package com.example.a7uvault.repository

import androidx.lifecycle.LiveData
import com.example.a7uvault.database.DocumentDao
import com.example.a7uvault.database.UserDocument

class DocumentRepository(private val documentDao: DocumentDao) {

    val allDocuments = documentDao.getAllDocuments()

    suspend fun addDocument(doc: UserDocument) {
        documentDao.addDocument(doc)
    }

    suspend fun deleteDocument(doc: UserDocument) {
        documentDao.deleteDocument(doc)
    }

    suspend fun deleteAllDocuments() {
        documentDao.deleteAllDocuments()
    }

    suspend fun updateDocument(doc: UserDocument) {
        documentDao.updateDocument(doc)
    }

    fun searchDocument(str: String):LiveData<List<UserDocument>> {
        return documentDao.searchDocument(str)
    }

}