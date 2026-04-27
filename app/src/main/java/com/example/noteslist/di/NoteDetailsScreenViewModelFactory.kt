package com.example.noteslist.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.noteslist.presentation.noteDetails.NoteDetailsDraftState
import com.example.noteslist.presentation.noteDetails.NoteDetailsViewModel
import java.util.UUID
import javax.inject.Inject

@ScreenScope
class NoteDetailsScreenViewModelFactory @Inject constructor(
    private val assistedFactory: NoteDetailsViewModel.Factory,
    private val noteId: UUID?,
    private val draftState: NoteDetailsDraftState?
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteDetailsViewModel::class.java)) {
            return assistedFactory.create(noteId, draftState) as T
        }
        throw IllegalArgumentException("Unknown model class $modelClass")
    }
}
