package com.example.noteslist

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.noteslist.databinding.NoteViewBinding
import com.example.noteslist.presentation.customviews.NoteView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val noteView = findViewById<NoteView>(R.id.my_note)

        noteView.apply {
            title = "Заметка на сегодня"
            content = "Не забыть покормить кота"
            time = "12:45"
            isImportant = true
            isRead = false
        }
    }
}