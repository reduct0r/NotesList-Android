package com.example.noteslist.domain

import com.example.noteslist.domain.model.Note

interface NoteRepository {
    fun getAllNotes(): List<Note>
}