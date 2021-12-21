package com.example.a7uvault.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.a7uvault.typeConverter.Converters

@Database(entities = [UserDocument::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class DocumentDatabase : RoomDatabase() {

    abstract fun getDocumentDao(): DocumentDao

    companion object {
        @Volatile
        private var INSTANCE: DocumentDatabase? = null

        fun getDatabase(context: Context): DocumentDatabase {
            val temp = INSTANCE
            if (temp != null) {
                return temp
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DocumentDatabase::class.java,
                    "document_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}