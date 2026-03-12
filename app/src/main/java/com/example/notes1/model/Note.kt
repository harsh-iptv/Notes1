package com.example.notes1.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import androidx.room.ColumnInfo
import androidx.room.Ignore
import java.util.*
@Entity(tableName = "Notes")
@Parcelize
data class Note(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,
    @ColumnInfo(name = "title")
    val title: String,
    @ColumnInfo(name = "content")
    val content: String,
    @ColumnInfo(name = "timestamp")
    val timestamp: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "color")
    val color: Int = 0
) : Parcelable {

    fun getFormattedDate(): String {
        val sdf = SimpleDateFormat("MMM dd, yyyy  hh:mm a", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }

    fun getPreview(): String {
        return if (content.length > 100) content.substring(0, 100) + "..." else content
    }
}
