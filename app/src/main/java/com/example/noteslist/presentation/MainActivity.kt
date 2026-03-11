package com.example.noteslist.presentation

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.noteslist.data.repository.NoteRepositoryImpl
import com.example.noteslist.databinding.ActivityMainBinding
import com.example.noteslist.domain.usecase.PrepareNoteListUseCase
import com.example.noteslist.presentation.adapter.NoteListAdapter


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: NoteListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = NoteListAdapter { note ->
            Toast.makeText(this, "Clicked: ${note.title}", Toast.LENGTH_SHORT).show()
        }

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }

        val repository = NoteRepositoryImpl()
        val useCase = PrepareNoteListUseCase()
        val items = useCase(repository.getAllNotes())
        adapter.submitList(items)
    }
}