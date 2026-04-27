package com.example.noteslist.data.mapper

import com.example.noteslist.data.local.entity.NoteEntity
import com.example.noteslist.domain.model.Note
import java.util.UUID

fun NoteEntity.toDomain(): Note {
    return Note(
        id = UUID.fromString(id),
        title = title,
        content = content,
        createdAt = createdAt,
        isImportant = isImportant,
        isRead = isRead
    )
}

fun Note.toEntity(): NoteEntity {
    return NoteEntity(
        id = requireNotNull(id) { "Note id is required to map Note to NoteEntity" }.toString(),
        title = title,
        content = content,
        createdAt = createdAt,
        isImportant = isImportant,
        isRead = isRead
    )
}
