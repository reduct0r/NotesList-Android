package com.example.noteslist.domain.repository

import com.example.noteslist.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    val notes: Flow<List<Note>>
    suspend fun addNote(note: Note)
    suspend fun updateNote(note: Note)
    suspend fun updateReadStatus(note: Note)
    suspend fun updateImportantStatus(note: Note)
}