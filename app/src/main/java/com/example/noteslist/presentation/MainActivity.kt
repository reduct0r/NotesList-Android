package com.example.noteslist.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.noteslist.data.repository.NoteRepository
import com.example.noteslist.data.repository.NoteRepositoryImpl
import com.example.noteslist.databinding.ActivityMainBinding

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
}