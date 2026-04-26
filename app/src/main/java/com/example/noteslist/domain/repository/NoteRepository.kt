package com.example.noteslist.domain.repository

import com.example.noteslist.domain.model.Note
import kotlinx.coroutines.flow.StateFlow

interface NoteRepository {
    val notes: StateFlow<List<Note>>
    fun addNote(note: Note)
    fun updateNote(note: Note)
}