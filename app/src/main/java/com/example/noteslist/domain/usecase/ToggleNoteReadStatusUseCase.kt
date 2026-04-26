package com.example.noteslist.domain.usecase

import com.example.noteslist.data.repository.NoteRepositoryImpl
import com.example.noteslist.domain.NoteRepository
import com.example.noteslist.domain.model.Note

class ToggleNoteReadStatusUseCase(
    private val repository: NoteRepository = NoteRepositoryImpl
) {
    operator fun invoke(note: Note) {
        repository.updateNote(note.copy(isRead = !note.isRead))
    }
}