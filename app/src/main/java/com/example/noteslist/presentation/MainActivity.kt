package com.example.noteslist.presentation

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.noteslist.databinding.ActivityMainBinding
import com.example.noteslist.presentation.adapter.NoteListAdapter
import com.example.noteslist.presentation.notesList.NoteListViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: NoteListAdapter
    private val viewModel: NoteListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fab.setOnClickListener { view ->
            Snackbar.make(view, "hello", Snackbar.LENGTH_LONG).show()
        }

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            private val threshold = 10
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val fab = binding.fab
                when {
                    dy > threshold && fab.isOrWillBeShown -> {
                        fab.hide()
                    }
                    dy < -threshold && fab.isOrWillBeShown -> {
                        fab.hide()
                    }
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                binding.fab.show()
            }
        })

        (binding.recyclerView.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false

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

