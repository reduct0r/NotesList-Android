package com.example.noteslist.domain.model.list

import com.example.noteslist.domain.model.Note

data class ImportantNoteItem(
    val note: Note
) : ListItem
