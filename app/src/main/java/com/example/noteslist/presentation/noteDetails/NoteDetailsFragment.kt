package com.example.noteslist.presentation.noteDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs

class NoteDetailsFragment: Fragment() {
    private val viewModel: NoteDetailsViewModel by viewModels()
    private val args: NoteDetailsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                NoteDetailsScreen(
                    viewModel = viewModel,
                    initialNote = args.note,
                    onNavigateBack = { findNavController().popBackStack() }
                )
            }
        }
    }
}
