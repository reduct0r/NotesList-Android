package com.example.noteslist.data.repository

import com.example.noteslist.domain.NoteRepository
import com.example.noteslist.domain.model.Note
import java.util.UUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class  NoteRepositoryImpl @Inject constructor(): NoteRepository {
    val now = System.currentTimeMillis()

    private val seedNotes = listOf(
        Note(UUID.randomUUID(), "ДЗ по Android", "RecyclerView & Delegates", now - HOUR, true, false),
        Note(UUID.randomUUID(), "ДЗ по АиСД", "Алгоритмы на графах", now - HOUR * 2, true, false),
        Note(UUID.randomUUID(), "Корм коту", "Купить влажный", now - HOUR * 3, false, false),
        Note(UUID.randomUUID(), "Зал", "Тренировка в 19:00", now - HOUR * 4, false, false),
        Note(UUID.randomUUID(), "Ужин", "Приготовить пасту", now - HOUR * 5, false, false),

        Note(UUID.randomUUID(), "Позвонить маме", "Поздравить!", now - DAY - HOUR, true, false),
        Note(UUID.randomUUID(), "Срочный баг", "Пофиксить в проде", now - DAY - HOUR * 2, true, false),

        Note(UUID.randomUUID(), "Купить молоко", "2.5%", now - DAY * 2 - HOUR, false, false),
        Note(UUID.randomUUID(), "Помыть машину", "Самообслуживание", now - DAY * 2 - HOUR * 3, false, false),

        Note(UUID.randomUUID(), "Заметка 1", "Текст", now - DAY * 3 - HOUR, false, false),
        Note(UUID.randomUUID(), "Заметка 2", "Текст", now - DAY * 3 - HOUR * 2, false, false),
        Note(UUID.randomUUID(), "Заметка 3", "Текст", now - DAY * 3 - HOUR * 3, false, false),
        Note(UUID.randomUUID(), "Заметка 4", "Текст", now - DAY * 3 - HOUR * 4, false, false),
        Note(UUID.randomUUID(), "Заметка 5", "Текст", now - DAY * 3 - HOUR * 5, false, false),

        Note(UUID.randomUUID(), "Одинокая заметка", "Я одна в этом дне", now - DAY * 4, false, false),

        Note(UUID.randomUUID(), "Старая важная", "Проверка архива", now - DAY * 7, true, false),
        Note(UUID.randomUUID(), "Старая обычная", "Проверка архива", now - DAY * 7 - HOUR, false, false),

        Note(UUID.randomUUID(), "Прошлый год", "С Новым Годом!", now - DAY * 400, false, false)
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

    private companion object {
        private const val HOUR = 3600000L
        private const val DAY = 86400000L
    }

}