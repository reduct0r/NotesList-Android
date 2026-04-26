package com.example.noteslist.domain.usecase

import com.example.noteslist.domain.model.Note
import com.example.noteslist.domain.model.getDateString
import com.example.noteslist.domain.model.list.DateHeaderItem
import com.example.noteslist.domain.model.list.ImportantNoteItem
import com.example.noteslist.domain.model.list.ListItem
import com.example.noteslist.domain.model.list.NoteStackItem
import jakarta.inject.Inject

class PrepareNoteListUseCase @Inject constructor() {

    operator fun invoke(notes: List<Note>): List<ListItem> {
        val groupedByDate = notes
            .groupBy { it.getDateString() }
            .entries
            .sortedByDescending { (_, notesOnDate) ->
                notesOnDate.maxOfOrNull { it.createdAt } ?: Long.MIN_VALUE
            }

        val result = mutableListOf<ListItem>()

        groupedByDate.forEach { (date, notesOnDate) ->
            result.add(DateHeaderItem(date))

            notesOnDate.filter { it.isImportant }
                .forEach { note ->
                    result.add(ImportantNoteItem(note))
                }

            val ordinary = notesOnDate.filterNot { it.isImportant }
            if (ordinary.isNotEmpty()) {
                result.add(NoteStackItem(ordinary))
            }
        }

        return result
    }
}