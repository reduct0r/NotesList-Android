package com.example.noteslist.data.repository

import com.example.noteslist.domain.NoteRepository
import com.example.noteslist.domain.model.Note

class NoteRepositoryImpl : NoteRepository {
    override fun getAllNotes(): List<Note> {
        val now = System.currentTimeMillis()
        val hour = 3600000L
        val day = 86400000L

        return listOf(
            Note(1, "Сдать ДЗ по Android", "Кастомные View и RecyclerView", now - hour, true),
            Note(2, "Купить корм коту", "Взять две пачки влажного", now - hour * 2, false),
            Note(3, "Записаться в зал", "Пробная тренировка на 19:00", now - hour * 5, false),

            Note(4, "Позвонить маме", "Поздравить с прошедшим", now - day - hour, true),
            Note(5, "Забрать посылку", "Код для получения: 5544", now - day - hour * 4, false),
            Note(6, "Помыть машину", "Записаться на мойку самообслуживания", now - day - hour * 8, false),

            Note(7, "Купить молоко", "Обязательно 2,5%, свежее", now - day * 2 - hour, false),
            Note(8, "Подготовить отчет", "Скинуть в Slack до обеда", now - day * 2 - hour * 6, true),

            Note(9, "Записаться к врачу", "Терапевт, 15 марта в 10:00", now - day * 3 - hour, true),
            Note(10, "Посмотреть лекцию", "Тема: Анимации в Android", now - day * 3 - hour * 3, false),

            Note(11, "Идея для пет-проекта", "Приложение для трекинга привычек", now - day * 7, false),
            Note(12, "Заказать пиццу", "Вечерний чилл с друзьями", now - day * 7 - hour * 5, false)
        )
    }
}