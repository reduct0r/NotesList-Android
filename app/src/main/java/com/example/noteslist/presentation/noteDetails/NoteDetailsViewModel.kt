package com.example.noteslist.presentation.noteDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteslist.data.repository.NoteRepositoryImpl
import com.example.noteslist.domain.model.Note
import com.example.noteslist.domain.model.isNew
import com.example.noteslist.domain.usecase.CreateNewNoteUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import jakarta.inject.Inject
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NoteDetailsViewModel @AssistedInject constructor(
    private val repository: NoteRepositoryImpl,
    @Assisted private val noteId: UUID?
) : ViewModel() {

    private val createNewNoteUseCase = CreateNewNoteUseCase()

    private val _uiState = MutableStateFlow(NoteDetailsUiState())
    val uiState: StateFlow<NoteDetailsUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<UiEvent>()
    val events: SharedFlow<UiEvent> = _events.asSharedFlow()

    private val newNoteTemplate: Note = createNewNoteUseCase()

    init {
        if (noteId == null) {
            _uiState.value = NoteDetailsUiState(
                isNewNote = true,
                title = "",
                content = "",
                isImportant = false,
                isRead = false
            )
        } else {
            viewModelScope.launch {
                repository.notes
                    .map { notes -> notes.firstOrNull { it.id == noteId } }
                    .distinctUntilChanged()
                    .collect { note ->
                        if (note == null) return@collect

                        _uiState.update {
                            it.copy(
                                currentNote = note,
                                title = note.title,
                                content = note.content,
                                isImportant = note.isImportant,
                                isRead = note.isRead,
                                isNewNote = false
                            )
                        }
                    }
            }
        }
    }

    // region UI events

    fun onTitleChanged(value: String) {
        _uiState.update { it.copy(title = value) }
    }

    fun onContentChanged(value: String) {
        _uiState.update { it.copy(content = value) }
    }

    fun onImportantChanged(value: Boolean) {
        _uiState.update { it.copy(isImportant = value) }
    }

    fun onReadChanged(value: Boolean) {
        _uiState.update { it.copy(isRead = value, isReadManuallyEdited = true) }
    }

    // endregion

    // region save

    fun onSaveClicked() {
        val state = _uiState.value
        if (state.title.isBlank()) return

        val note = buildNoteFromState(state)

        viewModelScope.launch {
            if (note.isNew()) {
                repository.addNote(note.copy(id = UUID.randomUUID()))
            } else {
                repository.updateNote(note)
            }

            _events.emit(UiEvent.NavigateBack)
        }
    }

    fun onBackPressedRequested() {
        val state = _uiState.value
        if (state.hasChanges) {
            _uiState.update { it.copy(showUnsavedDialog = true) }
            return
        }

        navigateBack()
    }

    fun dismissUnsavedDialog() {
        _uiState.update { it.copy(showUnsavedDialog = false) }
    }

    fun confirmCloseWithoutSaving() {
        _uiState.update { it.copy(showUnsavedDialog = false) }
        navigateBack()
    }

    // endregion

    // region helpers

    private fun buildNoteFromState(state: NoteDetailsUiState): Note {
        val base = state.currentNote ?: newNoteTemplate
        return base.copy(
            title = state.title,
            content = state.content,
            isImportant = state.isImportant,
            isRead = state.isRead
        )
    }

    fun clearDraft() {
        _uiState.value = NoteDetailsUiState()
    }

    private fun navigateBack() {
        viewModelScope.launch {
            _events.emit(UiEvent.NavigateBack)
        }
    }

    // endregion

    sealed interface UiEvent {
        data object NavigateBack : UiEvent
    }

    @AssistedFactory
    interface Factory {
        fun create(noteId: UUID?): NoteDetailsViewModel
    }
}