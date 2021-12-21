package com.example.a7uvault.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DocumentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addDocument(document: UserDocument)

    @Delete
    suspend fun deleteDocument(document: UserDocument)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateDocument(document: UserDocument)

    @Query("SELECT * FROM user_document_table ORDER BY timeInMillis DESC")
    fun getAllDocuments(): LiveData<List<UserDocument>>

    @Query("DELETE FROM user_document_table")
    suspend fun deleteAllDocuments()

    @Query("SELECT * FROM user_document_table WHERE userName LIKE :searchQuery OR documentType LIKE :searchQuery")
    fun searchDocument(searchQuery: String): LiveData<List<UserDocument>>

}