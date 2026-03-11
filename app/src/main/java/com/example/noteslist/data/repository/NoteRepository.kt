package com.example.noteslist.data.repository

import com.example.noteslist.domain.model.Note

interface NoteRepository {
    fun getAllNotes(): List<Note>
}
