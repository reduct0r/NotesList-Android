package com.example.noteslist.domain.model.list

import com.example.noteslist.domain.model.Note

data class NoteStackItem(
    val notes: List<Note>,
    val isExpanded: Boolean = false,
    val shouldAnimateExpand: Boolean = false
) : ListItem