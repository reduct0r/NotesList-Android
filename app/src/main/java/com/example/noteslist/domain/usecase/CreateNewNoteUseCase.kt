package com.example.noteslist.domain.usecase

import com.example.noteslist.domain.common.AppClock
import com.example.noteslist.domain.model.Note
import jakarta.inject.Inject

class CreateNewNoteUseCase @Inject constructor(
    private val appClock: AppClock
) {

    operator fun invoke(): Note {
        return Note(
            id = null,
            title = "",
            content = "",
            createdAt = appClock.currentTimeMillis(),
            isImportant = false,
            isRead = false
        )
    }
}
