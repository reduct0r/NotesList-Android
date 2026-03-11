package com.example.noteslist.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.noteslist.domain.NoteRepository
import com.example.noteslist.data.repository.NoteRepositoryImpl
import com.example.noteslist.databinding.ActivityMainBinding
import com.example.noteslist.domain.model.Note
import com.example.noteslist.domain.model.list.ImportantNoteItem
import com.example.noteslist.domain.model.list.ListItem
import com.example.noteslist.domain.model.list.NoteStackItem

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository: NoteRepository = NoteRepositoryImpl()
        val firstNote = repository.getAllNotes().firstOrNull()
        val allNotes = repository.getAllNotes()

//        firstNote?.let { note ->
//            binding.myNote.apply {
//                title = note.title
//                content = note.content
//                time = note.getTimeString()
//                isRead = note.isRead
//            }
//        }

        binding.myStack.setNotes(allNotes.take(5))
    }

    private fun prepareListItems(notes: List<Note>): List<ListItem> {
        val grouped = notes
            .groupBy { it.getDateString() }
            .toSortedMap(compareByDescending { it })

        val items = mutableListOf<ListItem>()

        grouped.forEach { (_, notesOnDate) ->
            notesOnDate.filter { it.isImportant }
                .forEach { note ->
                    items.add(ImportantNoteItem(note))
                }

            val ordinaryNotes = notesOnDate.filterNot { it.isImportant }
            if (ordinaryNotes.isNotEmpty()) {
                items.add(NoteStackItem(ordinaryNotes))
            }
        }

        return items
    }
}