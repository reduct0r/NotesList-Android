package com.example.noteslist.presentation.noteDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteslist.data.repository.NoteRepositoryImpl
import com.example.noteslist.domain.model.Note
import com.example.noteslist.domain.model.isNew
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class NoteDetailsViewModel : ViewModel() {

    private val repository = NoteRepositoryImpl
    private val _events = MutableSharedFlow<UiEvent>()
    val events: SharedFlow<UiEvent> = _events.asSharedFlow()

    fun observeNote(noteId: Long) = repository.notes
        .map { notes -> notes.firstOrNull { it.id == noteId } }
        .distinctUntilChanged()

    fun saveNote(note: Note) {
        viewModelScope.launch {
            if (note.isNew()) {
                repository.addNote(note.copy(id = System.currentTimeMillis()))
            } else {
                repository.updateNote(note)
            }
            _events.emit(UiEvent.NavigateBack)
        }
    }

    sealed interface UiEvent {
        data object NavigateBack : UiEvent
    }
}