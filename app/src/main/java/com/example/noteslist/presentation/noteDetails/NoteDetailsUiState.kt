package com.example.noteslist.presentation.noteDetails

import com.example.noteslist.domain.model.Note

data class NoteDetailsUiState(
    val currentNote: Note? = null,
    val title: String = "",
    val content: String = "",
    val isImportant: Boolean = false,
    val isRead: Boolean = false,
    val isReadManuallyEdited: Boolean = false,
    val showUnsavedDialog: Boolean = false,
    val isNewNote: Boolean = true,
    val isSaving: Boolean = false
) {
    val hasChanges: Boolean
        get() = title != (currentNote?.title ?: "") ||
                content != (currentNote?.content ?: "") ||
                isImportant != (currentNote?.isImportant ?: false) ||
                isRead != (currentNote?.isRead ?: false)
}
