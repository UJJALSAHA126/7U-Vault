package com.example.a7uvault.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.a7uvault.database.DocumentDatabase
import com.example.a7uvault.database.UserDocument
import com.example.a7uvault.repository.DocumentRepository
import kotlinx.coroutines.launch

class DocumentViewModel(application: Application) : AndroidViewModel(application) {

    val allDocuments: LiveData<List<UserDocument>>
    private val documentRepository: DocumentRepository

    init {
        val documentDao = DocumentDatabase.getDatabase(application).getDocumentDao()
        documentRepository = DocumentRepository(documentDao)
        allDocuments = documentRepository.allDocuments
    }

    fun addDocument(doc: UserDocument) {
        viewModelScope.launch {
            documentRepository.addDocument(doc)
        }
    }

    fun deleteDocument(doc: UserDocument) {
        viewModelScope.launch {
            documentRepository.deleteDocument(doc)
        }
    }

    fun updateDocument(doc: UserDocument) {
        viewModelScope.launch {
            documentRepository.updateDocument(doc)
        }
    }

    fun deleteAllDocument() {
        viewModelScope.launch {
            documentRepository.deleteAllDocuments()
        }
    }

    fun searchDocument(searchQuery: String): LiveData<List<UserDocument>> {
        return documentRepository.searchDocument(searchQuery)
    }

}