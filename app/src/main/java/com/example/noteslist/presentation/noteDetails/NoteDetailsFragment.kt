package com.example.noteslist.presentation.noteDetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.noteslist.NoteListApp
import com.example.noteslist.domain.model.Note
import com.example.noteslist.presentation.MainActivity
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
class NoteDetailsFragment : Fragment() {

    @Inject
    lateinit var vmFactory: NoteDetailsViewModel.Factory

    private val args: NoteDetailsFragmentArgs by navArgs()

    private val viewModel: NoteDetailsViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return vmFactory.create(args.note?.id) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (requireContext().applicationContext as NoteListApp)
            .appComponent
            .inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                NoteDetailsScreen(
                    viewModel = viewModel,
                    onNavigateBack = {
                        val hostActivity = activity as? MainActivity

                        if (hostActivity?.isTwoPaneMode() == true) {
                            hostActivity.closeNoteDetailsPane()
                        } else {
                            findNavController().popBackStack()
                        }
                    }
                )
            }
        }
    }

    fun getCurrentNoteForTransfer(): Note? {
        return viewModel.uiState.value.currentNote ?: args.note
    }
}
