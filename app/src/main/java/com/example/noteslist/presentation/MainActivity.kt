package com.example.noteslist.presentation

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.noteslist.databinding.ActivityMainBinding
import com.example.noteslist.presentation.adapter.NoteListAdapter
import com.example.noteslist.presentation.customviews.note.NoteViewModel
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: NoteListAdapter
    private val viewModel: NoteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = NoteListAdapter(
            onNoteClick = { viewModel.toggleNoteReadStatus(it.id) },
            onExpand = { viewModel.expandStack(it) },
            onCollapse = { viewModel.collapseStack(it) }
        )

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            this.adapter = this@MainActivity.adapter
        }

        lifecycleScope.launch {
            viewModel.uiItems.collect { items ->
                adapter.submitList(items)
            }
        }
    }
}

