package com.example.noteslist.data.repository

import com.example.noteslist.domain.NoteRepository
import com.example.noteslist.domain.model.Note

class NoteRepositoryImpl : NoteRepository {
    override fun getAllNotes(): List<Note> {
        val now = System.currentTimeMillis()
        val hour = 3600000L
        val day = 86400000L

        return listOf(
            Note(1, "ДЗ по Android", "RecyclerView & Delegates", now - hour, true),
            Note(2, "ДЗ по АиСД", "Алгоритмы на графах", now - hour * 2, true),
            Note(3, "Корм коту", "Купить влажный", now - hour * 3, false),
            Note(4, "Зал", "Тренировка в 19:00", now - hour * 4, false),
            Note(5, "Ужин", "Приготовить пасту", now - hour * 5, false),

            Note(6, "Позвонить маме", "Поздравить!", now - day - hour, true),
            Note(7, "Срочный баг", "Пофиксить в проде", now - day - hour * 2, true),

            Note(8, "Купить молоко", "2.5%", now - day * 2 - hour, false),
            Note(9, "Помыть машину", "Самообслуживание", now - day * 2 - hour * 3, false),

            Note(10, "Заметка 1", "Текст", now - day * 3 - hour, false),
            Note(11, "Заметка 2", "Текст", now - day * 3 - hour * 2, false),
            Note(12, "Заметка 3", "Текст", now - day * 3 - hour * 3, false),
            Note(13, "Заметка 4", "Текст", now - day * 3 - hour * 4, false),
            Note(14, "Заметка 5", "Текст", now - day * 3 - hour * 5, false),

            Note(15, "Одинокая заметка", "Я одна в этом дне", now - day * 4, false),

            Note(16, "Старая важная", "Проверка архива", now - day * 7, true),
            Note(17, "Старая обычная", "Проверка архива", now - day * 7 - hour, false),

            Note(18, "Прошлый год", "С Новым Годом!", now - day * 400, false)
        )
    }
}