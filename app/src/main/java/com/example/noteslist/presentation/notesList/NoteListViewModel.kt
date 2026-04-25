package com.example.noteslist.presentation.notesList

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.noteslist.data.repository.NoteRepositoryImpl
import com.example.noteslist.domain.model.Note
import com.example.noteslist.domain.model.list.ListItem
import com.example.noteslist.domain.model.list.NoteStackItem
import com.example.noteslist.domain.usecase.PrepareNoteListUseCase
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class NoteListViewModel : ViewModel() {

    private val repository = NoteRepositoryImpl
    private val prepareUseCase = PrepareNoteListUseCase()

    private val _uiItems = MutableStateFlow<List<ListItem>>(emptyList())
    private var allNotes: List<Note> = emptyList()

    val uiItems: StateFlow<List<ListItem>> = _uiItems.asStateFlow()

    private val expandedStacks = mutableMapOf<List<UUID>, Boolean>()
    private val pendingExpandAnimations = mutableSetOf<List<UUID>>()

    init {
        viewModelScope.launch {
            repository.notes.collect { notes ->
                allNotes = notes
                updateUiItems()
            }
        }
    }

    private fun updateUiItems() {
        val baseItems = prepareUseCase(allNotes)
        val consumedAnimationKeys = mutableListOf<List<UUID>>()

        val updatedItems = baseItems.map { item ->
            if (item is NoteStackItem) {
                val key = item.notes.mapNotNull { it.id }.sorted()
                val isExpanded = expandedStacks[key] ?: false
                val shouldAnimateExpand = isExpanded && pendingExpandAnimations.contains(key)

                if (shouldAnimateExpand) {
                    consumedAnimationKeys.add(key)
                }

                item.copy(
                    isExpanded = isExpanded,
                    shouldAnimateExpand = shouldAnimateExpand
                )
            } else {
                item
            }
        }

        pendingExpandAnimations.removeAll(consumedAnimationKeys.toSet())
        _uiItems.value = updatedItems
    }

    fun toggleNoteReadStatus(noteId: UUID?) {
        val targetId = noteId ?: return
        allNotes.firstOrNull { it.id == targetId }?.let { note ->
            repository.updateNote(note.copy(isRead = !note.isRead))
        }
    }

    fun expandStack(target: NoteStackItem) {
        val key = target.notes.mapNotNull { it.id }.sorted()
        expandedStacks[key] = true
        pendingExpandAnimations.add(key)
        updateUiItems()
    }

    fun collapseStack(target: NoteStackItem) {
        val key = target.notes.mapNotNull { it.id }.sorted()
        expandedStacks[key] = false
        pendingExpandAnimations.remove(key)
        updateUiItems()
    }
}