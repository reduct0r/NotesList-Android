package com.example.noteslist.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.noteslist.data.repository.NoteRepositoryImpl
import com.example.noteslist.databinding.ActivityMainBinding
import com.example.noteslist.domain.model.Note
import com.example.noteslist.domain.model.list.ImportantNoteItem
import com.example.noteslist.domain.model.list.NoteStackItem
import com.example.noteslist.domain.usecase.PrepareNoteListUseCase
import com.example.noteslist.presentation.adapter.delegates.ImportantNoteDelegate
import com.example.noteslist.presentation.adapter.NoteListAdapter
import com.example.noteslist.presentation.adapter.delegates.NoteStackDelegate

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: NoteListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = NoteListAdapter(
            onNoteClick = { updateNoteReadStatus(it) },
            onExpand = { expandStack(it) },
            onCollapse = { collapseStack(it) }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            this.adapter = this@MainActivity.adapter
        }

        val repository = NoteRepositoryImpl()
        val useCase = PrepareNoteListUseCase()
        val items = useCase(repository.getAllNotes())
        adapter.submitList(items)
    }

    private fun updateNoteReadStatus(clickedNote: Note) {
        val newList = adapter.currentList.map { listItem ->
            when (listItem) {
                is ImportantNoteItem -> {
                    if (listItem.note.id == clickedNote.id) {
                        ImportantNoteItem(
                            listItem.note.copy(isRead = !listItem.note.isRead)
                        )
                    } else listItem
                }

                is NoteStackItem -> {
                    val updatedNotes = listItem.notes.map { note ->
                        if (note.id == clickedNote.id) {
                            note.copy(isRead = !note.isRead)
                        } else note
                    }

                    listItem.copy(
                        notes = updatedNotes,
                        isExpanded = listItem.isExpanded
                    )
                }

                else -> listItem
            }
        }

        adapter.submitList(newList)
    }

    private fun expandStack(target: NoteStackItem) {
        val newList = adapter.currentList.map {
            if (it is NoteStackItem && it.notes == target.notes) {
                it.copy(isExpanded = true)
            } else {
                it
            }
        }
        adapter.submitList(newList)
    }

    private fun collapseStack(target: NoteStackItem) {
        val newList = adapter.currentList.map {
            if (it is NoteStackItem && it.notes == target.notes) {
                it.copy(isExpanded = false)
            } else it
        }
        adapter.submitList(newList)
    }
}

