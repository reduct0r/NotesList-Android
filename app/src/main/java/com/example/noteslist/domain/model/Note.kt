package com.example.noteslist.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Parcelize
data class Note(
    val id: Long = -1L,
    val title: String = "",
    val content: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isImportant: Boolean = false,
    val isRead: Boolean = false
) : Parcelable

fun Note.getTimeString(): String {
    return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(this.createdAt))
}

fun Note.getDateString(): String {
    return SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(this.createdAt))
}

fun Note.isNew(): Boolean = id == -1L