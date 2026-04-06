package com.example.noteslist.presentation.customviews

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

    init {
        updateUiItems()
    }

    private fun updateUiItems() {
        val baseItems = prepareUseCase(_allNotes.value)
        val updatedItems = baseItems.map { item ->
            if (item is NoteStackItem) {
                val key = item.notes.map { it.id }.sorted()
                val isExpanded = expandedStacks[key] ?: false
                item.copy(isExpanded = isExpanded)
            } else {
                item
            }
        }
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
        updateUiItems()
    }

    fun collapseStack(target: NoteStackItem) {
        val key = target.notes.map { it.id }.sorted()
        expandedStacks[key] = false
        updateUiItems()
    }
}