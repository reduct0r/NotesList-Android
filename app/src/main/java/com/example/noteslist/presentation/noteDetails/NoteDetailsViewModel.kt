package com.example.noteslist.presentation.noteDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteslist.di.ApplicationScope
import com.example.noteslist.domain.repository.NoteRepository
import com.example.noteslist.domain.model.Note
import com.example.noteslist.domain.model.isNew
import com.example.noteslist.domain.usecase.CreateNewNoteUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import java.util.UUID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
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
import kotlinx.coroutines.withContext

class NoteDetailsViewModel @AssistedInject constructor(
    @param:ApplicationScope private val applicationScope: CoroutineScope,
    private val repository: NoteRepository,
    private val createNewNoteUseCase: CreateNewNoteUseCase,
    @Assisted private val noteId: UUID?,
    @Assisted private val initialDraftState: NoteDetailsDraftState?
) : ViewModel() {
    private val _uiState = MutableStateFlow(NoteDetailsUiState())
    val uiState: StateFlow<NoteDetailsUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<UiEvent>()
    val events: SharedFlow<UiEvent> = _events.asSharedFlow()

    init {
        observeTitleValidation()

        when {
            initialDraftState != null -> {
                _uiState.value = initialDraftState.toUiState()
                if (noteId != null) {
                    observeExistingNote(restoreDraft = true)
                }
            }
            noteId == null -> {
                _uiState.value = NoteDetailsUiState(
                    isNewNote = true,
                    title = "",
                    content = "",
                    isImportant = false,
                    isRead = false
                )
            }
            else -> observeExistingNote(restoreDraft = false)
        }
    }

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

    fun onSaveClicked() {
        val state = _uiState.value
        if (!state.canSave) return

        val note = buildNoteFromState(state)
        _uiState.update { it.copy(isSaving = true) }

        applicationScope.launch {
            runCatching {
                if (note.isNew()) {
                    repository.addNote(note)
                } else {
                    repository.updateNote(note)
                }
            }
        }

        navigateBack()
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

    private fun observeTitleValidation() {
        viewModelScope.launch {
            _uiState
                .map { it.title }
                .distinctUntilChanged()
                .map { title ->
                    withContext(Dispatchers.Default) {
                        title.codePointCount(0, title.length) > TITLE_MAX_LENGTH
                    }
                }
                .collect { isTitleTooLong ->
                    _uiState.update { currentState ->
                        currentState.copy(isTitleTooLong = isTitleTooLong)
                    }
                }
        }
    }

    private fun observeExistingNote(restoreDraft: Boolean) {
        viewModelScope.launch {
            repository.notes
                .map { notes -> notes.firstOrNull { it.id == noteId } }
                .distinctUntilChanged()
                .collect { note ->
                    if (note == null) return@collect

                    _uiState.update {
                        if (restoreDraft) {
                            it.copy(
                                currentNote = note,
                                isNewNote = false
                            )
                        } else {
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

    private fun buildNoteFromState(state: NoteDetailsUiState): Note {
        val base = state.currentNote ?: createNewNoteUseCase()
        return base.copy(
            title = state.title,
            content = state.content,
            isImportant = state.isImportant,
            isRead = state.isRead
        )
    }

    private fun navigateBack() {
        viewModelScope.launch {
            _events.emit(UiEvent.NavigateBack)
        }
    }

    sealed interface UiEvent {
        data object NavigateBack : UiEvent
    }

    @AssistedFactory
    interface Factory {
        fun create(
            noteId: UUID?,
            initialDraftState: NoteDetailsDraftState?
        ): NoteDetailsViewModel
    }

    companion object {
        const val TITLE_MAX_LENGTH = 50
    }
}
