package com.example.a7uvault.database

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "user_document_table")
@Parcelize
data class UserDocument(
    var frontImageUri: String = "",
    var backImageUri: String = "",
    var imgFront: Bitmap? = null,
    var imgBack: Bitmap? = null,
    var userName: String = "",
    var documentType: String = "",
    var timeInMillis: Long = 0
) : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}