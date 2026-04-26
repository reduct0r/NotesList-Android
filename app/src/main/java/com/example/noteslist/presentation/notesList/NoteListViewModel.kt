package com.example.noteslist.presentation.notesList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteslist.data.repository.NoteRepositoryImpl
import com.example.noteslist.domain.repository.NoteRepository
import com.example.noteslist.domain.model.list.ListItem
import com.example.noteslist.domain.model.list.NoteStackItem
import com.example.noteslist.domain.usecase.BuildNoteListUiUseCase
import com.example.noteslist.domain.usecase.PrepareNoteListUseCase
import com.example.noteslist.domain.usecase.ToggleNoteReadStatusUseCase
import com.example.noteslist.domain.usecase.ToggleStackUseCase
import jakarta.inject.Inject
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class NoteListViewModel @Inject constructor(
    private val repository: NoteRepository,
    private val prepareUseCase: PrepareNoteListUseCase,
    private val buildNoteListUiUseCase: BuildNoteListUiUseCase,
    private val toggleStackUseCase: ToggleStackUseCase,
    private val toggleNoteReadStatusUseCase: ToggleNoteReadStatusUseCase
) : ViewModel() {

    private val expandedStacks = MutableStateFlow<Map<List<UUID>, Boolean>>(emptyMap())
    private val pendingExpandAnimations = mutableSetOf<List<UUID>>()

    val uiItems: StateFlow<List<ListItem>> = combine(
        repository.notes,
        expandedStacks
    ) { notes, expanded ->
        val baseItems = prepareUseCase(notes)
        val (items, consumedAnimations) = buildNoteListUiUseCase(
            baseItems = baseItems,
            expandedStacks = expanded,
            pendingAnimations = pendingExpandAnimations
        )

        pendingExpandAnimations.removeAll(consumedAnimations)

        items
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun toggleNoteReadStatus(noteId: UUID?) {
        val targetId = noteId ?: return
        repository.notes.value.firstOrNull { it.id == targetId }?.let { note ->
            toggleNoteReadStatusUseCase(note)
        }
    }

    fun toggleStack(target: NoteStackItem) {
        val key = target.notes.mapNotNull { it.id }.sorted()
        expandedStacks.update { current ->
            current.toMutableMap().apply {
                toggleStackUseCase(
                    ToggleStackUseCase.Params(
                        key = key,
                        expanded = this,
                        animations = pendingExpandAnimations
                    )
                )
            }
        }
    }
}