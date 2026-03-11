package com.example.noteslist.data.repository

import com.example.noteslist.domain.model.Note

class NoteRepositoryImpl : NoteRepository {
    override fun getAllNotes(): List<Note> {
        return listOf(
            Note(
                id = 1,
                title = "Купить молоко",
                content = "Обязательно 2,5%, свежее",
                createdAt = System.currentTimeMillis() - 86400000 * 2,
                isImportant = false
            ),
            Note(
                id = 2,
                title = "Позвонить маме",
                content = "Поздравить с днём рождения",
                createdAt = System.currentTimeMillis() - 3600000 * 5,
                isImportant = true
            ),
            Note(
                id = 3,
                title = "Сдать ДЗ по Android",
                content = "Кастомные View NoteView и NoteStackView",
                createdAt = System.currentTimeMillis(),
                isImportant = false
            ),
            Note(
                id = 4,
                title = "Забрать посылку",
                content = "Срок до 18:00",
                createdAt = System.currentTimeMillis() - 86400000,
                isImportant = false
            ),
            Note(
                id = 5,
                title = "Записаться к врачу",
                content = "Терапевт, 15 марта",
                createdAt = System.currentTimeMillis() - 86400000 * 3,
                isImportant = true
            )
        )
    }
}