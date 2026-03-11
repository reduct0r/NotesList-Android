package com.example.noteslist.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.noteslist.data.repository.NoteRepositoryImpl
import com.example.noteslist.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val repository = NoteRepositoryImpl()
        val notes = repository.getAllNotes()

        binding.tvInfo.text = notes.joinToString("\n") { "${it.title} - ${it.getTimeString()}" }
    }
}