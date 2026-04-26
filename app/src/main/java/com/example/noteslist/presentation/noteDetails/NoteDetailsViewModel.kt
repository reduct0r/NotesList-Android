package com.example.noteslist.presentation.noteDetails

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteslist.data.repository.NoteRepositoryImpl
import com.example.noteslist.domain.model.Note
import com.example.noteslist.domain.model.isNew
import com.example.noteslist.domain.usecase.CreateNewNoteUseCase
import jakarta.inject.Inject
import java.util.UUID
import kotlinx.coroutines.Job
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

class NoteDetailsViewModel @Inject constructor(
    private val repository: NoteRepositoryImpl
): ViewModel() {
    private val createNewNoteUseCase = CreateNewNoteUseCase()
    private val _uiState = MutableStateFlow(NoteDetailsUiState())
    val uiState: StateFlow<NoteDetailsUiState> = _uiState.asStateFlow()
    private val _events = MutableSharedFlow<UiEvent>()
    val events: SharedFlow<UiEvent> = _events.asSharedFlow()
    private var observeNoteJob: Job? = null
    private var newNoteTemplate: Note = createNewNoteUseCase()
    private var hasInitialized = false
    private var initializedNoteId: UUID? = null

    fun initialize(initialNote: Note?) {
        val noteId = initialNote?.id
        if (hasInitialized && initializedNoteId == noteId) {
            return
        }

        val wasInitialized = hasInitialized
        hasInitialized = true
        observeNoteJob?.cancel()
        initializedNoteId = noteId

        val baseNote = if (wasInitialized) {
            buildDraftFromState(_uiState.value)
        } else {
            initialNote
        }
        val isNewNote = baseNote?.isNew() ?: true
        if (isNewNote) {
            newNoteTemplate = baseNote ?: createNewNoteUseCase()
        }

        _uiState.value = NoteDetailsUiState(
            currentNote = baseNote?.takeUnless { it.isNew() },
            title = baseNote?.title.orEmpty(),
            content = baseNote?.content.orEmpty(),
            isImportant = baseNote?.isImportant ?: false,
            isRead = baseNote?.isRead ?: false,
            isReadManuallyEdited = false,
            showUnsavedDialog = false,
            isNewNote = isNewNote
        )

        val existingNoteId = initialNote?.takeIf { !it.isNew() }?.id ?: return
        observeNoteJob = viewModelScope.launch {
            observeNote(existingNoteId).collect { updatedNote ->
                if (updatedNote == null) return@collect
                _uiState.update { state ->
                    state.copy(
                        currentNote = updatedNote,
                        isRead = if (state.isReadManuallyEdited) state.isRead else updatedNote.isRead,
                        isNewNote = false
                    )
                }
            }
        }
    }

    fun onTitleChanged(value: String) {
        updateState { it.copy(title = value) }
    }

    fun onContentChanged(value: String) {
        updateState { it.copy(content = value) }
    }

    fun onImportantChanged(value: Boolean) {
        updateState { it.copy(isImportant = value) }
    }

    fun onReadChanged(value: Boolean) {
        updateState { it.copy(isRead = value, isReadManuallyEdited = true) }
    }

    fun onBackPressedRequested() {
        if (_uiState.value.hasChanges) {
            _uiState.update { it.copy(showUnsavedDialog = true) }
            return
        }
        navigateBack()
    }

    fun dismissUnsavedDialog() {
        _uiState.update { it.copy(showUnsavedDialog = false) }
    }

    fun confirmCloseWithoutSaving() {
        navigateBack()
    }

    fun onSaveClicked() {
        val state = _uiState.value
        if (state.title.isBlank()) return

        val noteToSave = buildDraftFromState(state).copy(
            title = state.title.trim(),
            content = state.content.trim(),
            isImportant = state.isImportant,
            isRead = state.isRead
        )
        saveNote(noteToSave)
    }

    fun getDraftOrInitial(initial: Note?): Note? {
        if (!hasInitialized) return initial
        return buildDraftFromState(_uiState.value)
    }

    fun clearDraft() {
        observeNoteJob?.cancel()
        hasInitialized = false
        initializedNoteId = null
        newNoteTemplate = createNewNoteUseCase()
        _uiState.value = NoteDetailsUiState()
    }

    fun observeNote(noteId: UUID) = repository.notes
        .map { notes -> notes.firstOrNull { it.id == noteId } }
        .distinctUntilChanged()

    fun saveNote(note: Note) {
        viewModelScope.launch {
            if (note.isNew()) {
                repository.addNote(note.copy(id = generateNoteId()))
            } else {
                repository.updateNote(note)
            }
            clearDraft()
            _events.emit(UiEvent.NavigateBack)
        }
    }

    private fun generateNoteId(): UUID = UUID.randomUUID()

    private fun updateState(transform: (NoteDetailsUiState) -> NoteDetailsUiState) {
        _uiState.update(transform)
    }

    private fun buildDraftFromState(state: NoteDetailsUiState): Note {
        val baseNote = state.currentNote ?: newNoteTemplate
        return baseNote.copy(
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
}
