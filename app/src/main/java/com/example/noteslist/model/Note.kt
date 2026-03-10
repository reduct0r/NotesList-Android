package com.example.noteslist.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class Note(
    val id: Long,
    val title: String,
    val content: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isImportant: Boolean = false,
    var isRead: Boolean = false
) {
    fun getTimeString(): String {
        return SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(createdAt))
    }
}