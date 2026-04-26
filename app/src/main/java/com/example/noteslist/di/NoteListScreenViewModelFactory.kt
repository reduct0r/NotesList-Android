package com.example.noteslist.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.noteslist.presentation.notesList.NoteListViewModel
import javax.inject.Inject
import javax.inject.Provider

@ScreenScope
class NoteListScreenViewModelFactory @Inject constructor(
    private val noteListViewModelProvider: Provider<NoteListViewModel>
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteListViewModel::class.java)) {
            return noteListViewModelProvider.get() as T
        }
        throw IllegalArgumentException("Unknown model class $modelClass")
    }
}
