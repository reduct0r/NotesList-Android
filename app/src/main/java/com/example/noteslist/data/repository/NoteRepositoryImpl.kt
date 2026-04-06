package com.example.noteslist.data.repository

import com.example.noteslist.domain.NoteRepository
import com.example.noteslist.domain.model.Note
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object NoteRepositoryImpl : NoteRepository {
    val now = System.currentTimeMillis()
    const val HOUR = 3600000L
    const val DAY = 86400000L

    private val seedNotes = listOf(
        Note(1, "ДЗ по Android", "RecyclerView & Delegates", now - HOUR, true),
        Note(2, "ДЗ по АиСД", "Алгоритмы на графах", now - HOUR * 2, true),
        Note(3, "Корм коту", "Купить влажный", now - HOUR * 3, false),
        Note(4, "Зал", "Тренировка в 19:00", now - HOUR * 4, false),
        Note(5, "Ужин", "Приготовить пасту", now - HOUR * 5, false),

        Note(6, "Позвонить маме", "Поздравить!", now - DAY - HOUR, true),
        Note(7, "Срочный баг", "Пофиксить в проде", now - DAY - HOUR * 2, true),

        Note(8, "Купить молоко", "2.5%", now - DAY * 2 - HOUR, false),
        Note(9, "Помыть машину", "Самообслуживание", now - DAY * 2 - HOUR * 3, false),

        Note(10, "Заметка 1", "Текст", now - DAY * 3 - HOUR, false),
        Note(11, "Заметка 2", "Текст", now - DAY * 3 - HOUR * 2, false),
        Note(12, "Заметка 3", "Текст", now - DAY * 3 - HOUR * 3, false),
        Note(13, "Заметка 4", "Текст", now - DAY * 3 - HOUR * 4, false),
        Note(14, "Заметка 5", "Текст", now - DAY * 3 - HOUR * 5, false),

        Note(15, "Одинокая заметка", "Я одна в этом дне", now - DAY * 4, false),

        Note(16, "Старая важная", "Проверка архива", now - DAY * 7, true),
        Note(17, "Старая обычная", "Проверка архива", now - DAY * 7 - HOUR, false),

        Note(18, "Прошлый год", "С Новым Годом!", now - DAY * 400, false)
    )

    private val _notes = MutableStateFlow(seedNotes)
    override val notes: StateFlow<List<Note>> = _notes.asStateFlow()

    override fun addNote(note: Note) {
        _notes.value += note
    }

    override fun updateNote(note: Note) {
        val current = _notes.value
        val index = current.indexOfFirst { it.id == note.id }
        if (index != -1) {
            _notes.value = current.toMutableList().apply {
                this[index] = note
            }
        }
    }
}