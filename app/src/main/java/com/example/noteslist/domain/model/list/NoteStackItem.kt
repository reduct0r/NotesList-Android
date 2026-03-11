package com.example.noteslist.domain.model.list

import com.example.noteslist.domain.model.Note

data class NoteStackItem(
    val notes: List<Note>
) : ListItem