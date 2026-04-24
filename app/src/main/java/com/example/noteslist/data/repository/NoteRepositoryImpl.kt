package com.example.noteslist.data.repository

import com.example.noteslist.domain.NoteRepository
import com.example.noteslist.domain.model.Note
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object NoteRepositoryImpl : NoteRepository {
    val now = System.currentTimeMillis()
    private const val HOUR = 3600000L
    private const val DAY = 86400000L

    private val seedNotes = listOf(
        Note(1L, "ДЗ по Android", "RecyclerView & Delegates", now - HOUR, true, false),
        Note(2L, "ДЗ по АиСД", "Алгоритмы на графах", now - HOUR * 2, true, false),
        Note(3L, "Корм коту", "Купить влажный", now - HOUR * 3, false, false),
        Note(4L, "Зал", "Тренировка в 19:00", now - HOUR * 4, false, false),
        Note(5L, "Ужин", "Приготовить пасту", now - HOUR * 5, false, false),

        Note(6L, "Позвонить маме", "Поздравить!", now - DAY - HOUR, true, false),
        Note(7L, "Срочный баг", "Пофиксить в проде", now - DAY - HOUR * 2, true, false),

        Note(8L, "Купить молоко", "2.5%", now - DAY * 2 - HOUR, false, false),
        Note(9L, "Помыть машину", "Самообслуживание", now - DAY * 2 - HOUR * 3, false, false),

        Note(10L, "Заметка 1", "Текст", now - DAY * 3 - HOUR, false, false),
        Note(11L, "Заметка 2", "Текст", now - DAY * 3 - HOUR * 2, false, false),
        Note(12L, "Заметка 3", "Текст", now - DAY * 3 - HOUR * 3, false, false),
        Note(13L, "Заметка 4", "Текст", now - DAY * 3 - HOUR * 4, false, false),
        Note(14L, "Заметка 5", "Текст", now - DAY * 3 - HOUR * 5, false, false),

        Note(15L, "Одинокая заметка", "Я одна в этом дне", now - DAY * 4, false, false),

        Note(16L, "Старая важная", "Проверка архива", now - DAY * 7, true, false),
        Note(17L, "Старая обычная", "Проверка архива", now - DAY * 7 - HOUR, false, false),

        Note(18L, "Прошлый год", "С Новым Годом!", now - DAY * 400, false, false)
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