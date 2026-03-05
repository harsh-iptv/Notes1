package com.example.notes1.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
data class Note(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
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
