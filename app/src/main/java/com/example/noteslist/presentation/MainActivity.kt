package com.example.noteslist.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.noteslist.data.repository.NoteRepositoryImpl
import com.example.noteslist.databinding.ActivityMainBinding
import com.example.noteslist.domain.model.Note
import com.example.noteslist.domain.model.list.ImportantNoteItem
import com.example.noteslist.domain.usecase.PrepareNoteListUseCase
import com.example.noteslist.presentation.adapter.delegates.ImportantNoteDelegate
import com.example.noteslist.presentation.adapter.NoteListAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: NoteListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = NoteListAdapter(
            ImportantNoteDelegate { clickedNote ->
                val newList = adapter.currentList.map { listItem ->
                    if (listItem is ImportantNoteItem && listItem.note.id == clickedNote.id) {
                        ImportantNoteItem(
                            listItem.note.copy(isRead = !listItem.note.isRead)
                        )
                    } else {
                        listItem
                    }
                }
                adapter.submitList(newList)
            }
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
}

