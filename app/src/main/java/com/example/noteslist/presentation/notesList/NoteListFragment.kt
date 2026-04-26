package com.example.noteslist.presentation.notesList

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.BundleCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.example.noteslist.NoteListApp
import com.example.noteslist.databinding.FragmentNoteListBinding
import com.example.noteslist.domain.model.Note
import com.example.noteslist.presentation.MainActivity
import com.example.noteslist.presentation.adapter.NoteListAdapter
import jakarta.inject.Inject
import kotlinx.coroutines.launch
import kotlin.getValue


class NoteListFragment: Fragment() {
    private var _binding: FragmentNoteListBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val viewModel by viewModels<NoteListViewModel> {
        viewModelFactory
    }

    private lateinit var adapter: NoteListAdapter
    private var pendingRecyclerState: Parcelable? = null
    private var pendingWasAtBottom: Boolean? = null
    private var isRecyclerStateRestored = false

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (requireContext().applicationContext as NoteListApp)
            .appComponent
            .noteListComponent()
            .create()
            .inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pendingRecyclerState = savedInstanceState?.let {
            BundleCompat.getParcelable(it, KEY_RECYCLER_STATE, Parcelable::class.java)
        }
        pendingWasAtBottom = savedInstanceState?.let {
            if (it.containsKey(KEY_RECYCLER_WAS_AT_BOTTOM)) {
                it.getBoolean(KEY_RECYCLER_WAS_AT_BOTTOM)
            } else {
                null
            }
        }
    }

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
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val fab = binding.fab
                when {
                    dy > FAB_SCROLL_HIDE_THRESHOLD && fab.isOrWillBeShown -> {
                        fab.hide()
                    }
                    dy < -FAB_SCROLL_HIDE_THRESHOLD && fab.isOrWillBeShown -> {
                        fab.hide()
                    }
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                binding.fab.show()
            }
        })

        binding.fab.setOnClickListener {
            openDetails(note = null)
        }

        adapter = NoteListAdapter(
            onNoteClick = { note ->
                openDetails(note)
            },
            onNoteLongClick = { note ->
                viewModel.toggleNoteReadStatus(note.id)
            },
            onToggleStack = { viewModel.toggleStack(it) }
        )

        binding.recyclerView.adapter = adapter

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiItems.collect { items ->
                    adapter.submitList(items)
                    restoreRecyclerStateIfNeeded()
                }
            }
        }
    }

    private fun restoreRecyclerStateIfNeeded() {
        val currentBinding = _binding ?: return
        if (isRecyclerStateRestored) return

        if (pendingWasAtBottom == true) {
            restoreRecyclerToBottom()
            return
        }

        val state = pendingRecyclerState ?: return

        currentBinding.recyclerView.post {
            if (isRecyclerStateRestored) return@post
            _binding?.recyclerView?.layoutManager?.onRestoreInstanceState(state)
            pendingRecyclerState = null
            pendingWasAtBottom = null
            isRecyclerStateRestored = true
        }
    }

    private fun restoreRecyclerToBottom() {
        val currentBinding = _binding ?: return
        currentBinding.recyclerView.post {
            if (isRecyclerStateRestored) return@post
            val lastIndex = adapter.itemCount - 1
            if (lastIndex >= 0) {
                _binding?.recyclerView?.scrollToPosition(lastIndex)
                _binding?.recyclerView?.post {
                    _binding?.recyclerView?.scrollToPosition(lastIndex)
                    pendingRecyclerState = null
                    pendingWasAtBottom = null
                    isRecyclerStateRestored = true
                }
            } else {
                pendingRecyclerState = null
                pendingWasAtBottom = null
                isRecyclerStateRestored = true
            }
        }
    }

    override fun onDestroyView() {
        pendingRecyclerState = binding.recyclerView.layoutManager?.onSaveInstanceState()
        pendingWasAtBottom = !binding.recyclerView.canScrollVertically(1)
        isRecyclerStateRestored = false
        super.onDestroyView()
        _binding = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(
            KEY_RECYCLER_STATE,
            pendingRecyclerState ?: _binding?.recyclerView?.layoutManager?.onSaveInstanceState()
        )
        outState.putBoolean(
            KEY_RECYCLER_WAS_AT_BOTTOM,
            pendingWasAtBottom ?: (_binding?.recyclerView?.canScrollVertically(1) == false)
        )
    }

    private fun openDetails(note: Note?) {
        val hostActivity = activity as? MainActivity
        if (hostActivity?.isTwoPaneMode() == true) {
            hostActivity.openNoteDetailsPane(note)
            return
        }

        val direction = NoteListFragmentDirections
            .actionNoteListFragmentToNoteDetailsFragment(note)
        findNavController().navigate(direction)
    }

    companion object {
        private const val FAB_SCROLL_HIDE_THRESHOLD = 0
        private const val KEY_RECYCLER_STATE = "key_recycler_state"
        private const val KEY_RECYCLER_WAS_AT_BOTTOM = "key_recycler_was_at_bottom"
    }
}
