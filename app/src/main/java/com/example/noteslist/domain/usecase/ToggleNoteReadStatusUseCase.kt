package com.example.noteslist.domain.usecase

import com.example.noteslist.domain.repository.NoteRepository
import com.example.noteslist.domain.model.Note
import jakarta.inject.Inject

class ToggleNoteReadStatusUseCase @Inject constructor(
    private val repository: NoteRepository
) {
    suspend operator fun invoke(note: Note) {
        repository.updateNote(note.copy(isRead = !note.isRead))
    }
}