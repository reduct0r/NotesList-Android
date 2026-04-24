package com.example.noteslist.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@Parcelize
data class Note(
    val id: UUID?,
    val title: String,
    val content: String,
    val createdAt: Long,
    val isImportant: Boolean,
    val isRead: Boolean
) : Parcelable

fun Note.getTimeString(): String {
    return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(this.createdAt))
}

fun Note.getDateString(): String {
    return SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(this.createdAt))
}

fun Note.isNew(): Boolean = id == null