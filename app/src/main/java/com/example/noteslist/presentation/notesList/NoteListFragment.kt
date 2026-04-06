package com.example.noteslist.presentation.notesList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.noteslist.R
import com.example.noteslist.databinding.FragmentNoteListBinding
import com.example.noteslist.presentation.adapter.NoteListAdapter
import kotlinx.coroutines.launch


class NoteListFragment: Fragment() {
    private var _binding: FragmentNoteListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: NoteListViewModel by viewModels()

    private lateinit var adapter: NoteListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNoteListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (binding.recyclerView.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false

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

        binding.fab.setOnClickListener {
            val direction = NoteListFragmentDirections
                .actionNoteListFragmentToNoteDetailsFragment(null)
            findNavController().navigate(direction)
        }

        adapter = NoteListAdapter(
            onNoteClick = { note ->
                val direction = NoteListFragmentDirections
                    .actionNoteListFragmentToNoteDetailsFragment(note)
                findNavController().navigate(direction)
            },
            onNoteLongClick = { note ->
                viewModel.toggleNoteReadStatus(note.id)
            },
            onExpand = { viewModel.expandStack(it) },
            onCollapse = { viewModel.collapseStack(it) }
        )

        binding.recyclerView.adapter = adapter

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
        }

        lifecycleScope.launch {
            viewModel.uiItems.collect { items ->
                adapter.submitList(items)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
