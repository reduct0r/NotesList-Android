package com.example.noteslist.presentation.customviews.note

import androidx.lifecycle.ViewModel
import com.example.noteslist.data.repository.NoteRepositoryImpl
import com.example.noteslist.domain.model.list.ListItem
import com.example.noteslist.domain.model.list.NoteStackItem
import com.example.noteslist.domain.usecase.PrepareNoteListUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NoteViewModel : ViewModel() {

    private val repository = NoteRepositoryImpl()
    private val prepareUseCase = PrepareNoteListUseCase()

    private val _allNotes = MutableStateFlow(repository.getAllNotes())
    private val _uiItems = MutableStateFlow<List<ListItem>>(emptyList())

    val uiItems: StateFlow<List<ListItem>> = _uiItems.asStateFlow()

    private val expandedStacks = mutableMapOf<List<Long>, Boolean>()
    private val pendingExpandAnimations = mutableSetOf<List<Long>>()

    init {
        updateUiItems()
    }

    private fun updateUiItems() {
        val baseItems = prepareUseCase(_allNotes.value)
        val consumedAnimationKeys = mutableListOf<List<Long>>()

        val updatedItems = baseItems.map { item ->
            if (item is NoteStackItem) {
                val key = item.notes.map { it.id }.sorted()
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

    fun toggleNoteReadStatus(noteId: Long) {
        _allNotes.value = _allNotes.value.map { note ->
            if (note.id == noteId) {
                note.copy(isRead = !note.isRead)
            } else {
                note
            }
        }
        updateUiItems()
    }

    fun expandStack(target: NoteStackItem) {
        val key = target.notes.map { it.id }.sorted()
        expandedStacks[key] = true
        pendingExpandAnimations.add(key)
        updateUiItems()
    }

    fun collapseStack(target: NoteStackItem) {
        val key = target.notes.map { it.id }.sorted()
        expandedStacks[key] = false
        pendingExpandAnimations.remove(key)
        updateUiItems()
    }
}