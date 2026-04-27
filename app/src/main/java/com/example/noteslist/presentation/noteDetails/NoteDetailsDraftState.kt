package com.example.noteslist.presentation.noteDetails

import android.os.Parcelable
import com.example.noteslist.domain.model.Note
import kotlinx.parcelize.Parcelize

@Parcelize
data class NoteDetailsDraftState(
    val originalNote: Note?,
    val title: String,
    val content: String,
    val isImportant: Boolean,
    val isRead: Boolean,
    val isReadManuallyEdited: Boolean,
    val isNewNote: Boolean,
    val showUnsavedDialog: Boolean
) : Parcelable

fun NoteDetailsDraftState.toUiState(): NoteDetailsUiState {
    return NoteDetailsUiState(
        currentNote = originalNote,
        title = title,
        content = content,
        isImportant = isImportant,
        isRead = isRead,
        isReadManuallyEdited = isReadManuallyEdited,
        showUnsavedDialog = showUnsavedDialog,
        isNewNote = isNewNote
    )
}

fun NoteDetailsUiState.toDraftState(fallbackNote: Note? = currentNote): NoteDetailsDraftState {
    return NoteDetailsDraftState(
        originalNote = currentNote ?: fallbackNote,
        title = title,
        content = content,
        isImportant = isImportant,
        isRead = isRead,
        isReadManuallyEdited = isReadManuallyEdited,
        isNewNote = isNewNote,
        showUnsavedDialog = showUnsavedDialog
    )
}
