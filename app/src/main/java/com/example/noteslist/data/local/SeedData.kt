package com.example.noteslist.data.local

import com.example.noteslist.data.local.entity.NoteEntity
import java.nio.charset.StandardCharsets
import java.util.UUID

object SeedData {

    private const val HOUR = 1000 * 60 * 60L
    private const val DAY = HOUR * 24

    private val now = System.currentTimeMillis()

    private fun seedId(suffix: String): String {
        return UUID.nameUUIDFromBytes("seed-note-$suffix".toByteArray(StandardCharsets.UTF_8)).toString()
    }

    fun notes(): List<NoteEntity> {
        return listOf(

            NoteEntity(seedId("1"), "ДЗ по Android", "RecyclerView & Delegates", now - HOUR, true, false),
            NoteEntity(seedId("2"), "ДЗ по АиСД", "Алгоритмы на графах", now - HOUR * 2, true, false),
            NoteEntity(seedId("3"), "Корм коту", "Купить влажный", now - HOUR * 3, false, false),
            NoteEntity(seedId("4"), "Зал", "Тренировка в 19:00", now - HOUR * 4, false, false),
            NoteEntity(seedId("5"), "Ужин", "Приготовить пасту", now - HOUR * 5, false, false),

            NoteEntity(seedId("6"), "Позвонить маме", "Поздравить!", now - DAY - HOUR, true, false),
            NoteEntity(seedId("7"), "Срочный баг", "Пофиксить в проде", now - DAY - HOUR * 2, true, false),

            NoteEntity(seedId("8"), "Купить молоко", "2.5%", now - DAY * 2 - HOUR, false, false),
            NoteEntity(seedId("9"), "Помыть машину", "Самообслуживание", now - DAY * 2 - HOUR * 3, false, false),

            NoteEntity(seedId("10"), "Заметка 1", "Текст", now - DAY * 3 - HOUR, false, false),
            NoteEntity(seedId("11"), "Заметка 2", "Текст", now - DAY * 3 - HOUR * 2, false, false),
            NoteEntity(seedId("12"), "Заметка 3", "Текст", now - DAY * 3 - HOUR * 3, false, false),
            NoteEntity(seedId("13"), "Заметка 4", "Текст", now - DAY * 3 - HOUR * 4, false, false),
            NoteEntity(seedId("14"), "Заметка 5", "Текст", now - DAY * 3 - HOUR * 5, false, false),

            NoteEntity(seedId("15"), "Одинокая заметка", "Я одна в этом дне", now - DAY * 4, false, false),

            NoteEntity(seedId("16"), "Старая важная", "Проверка архива", now - DAY * 7, true, false),
            NoteEntity(seedId("17"), "Старая обычная", "Проверка архива", now - DAY * 7 - HOUR, false, false),

            NoteEntity(seedId("18"), "Прошлый год", "С Новым Годом!", now - DAY * 400, false, false)
        )
    }
}
