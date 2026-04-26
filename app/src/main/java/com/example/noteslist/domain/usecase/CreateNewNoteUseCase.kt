package com.example.noteslist.domain.usecase

import com.example.noteslist.domain.model.Note
import jakarta.inject.Inject

class CreateNewNoteUseCase @Inject constructor() {

    operator fun invoke(now: Long = System.currentTimeMillis()): Note {
        return Note(
            id = null,
            title = "",
            content = "",
            createdAt = now,
            isImportant = false,
            isRead = false
        )
    }
}
