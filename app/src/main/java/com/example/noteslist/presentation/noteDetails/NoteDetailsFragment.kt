package com.example.noteslist.presentation.noteDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController

class NoteDetailsFragment: Fragment() {
    private val viewModel: NoteDetailsViewModel by viewModels()

    private val noteId: Int by lazy {
        arguments?.getInt("noteId") ?: -1
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                val note = viewModel.getNoteById(noteId)

                NoteDetailsScreen(
                    viewModel = viewModel,
                    initialNote = note,
                    onNavigateBack = { findNavController().popBackStack() }
                )
            }
        }
    }
}
