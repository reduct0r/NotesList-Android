package com.example.noteslist.presentation.noteDetails

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.noteslist.NoteListApp
import com.example.noteslist.presentation.MainActivity
import javax.inject.Inject

class NoteDetailsFragment : Fragment() {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val args: NoteDetailsFragmentArgs by navArgs()

    private val viewModel: NoteDetailsViewModel by viewModels {
        viewModelFactory
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (requireContext().applicationContext as NoteListApp)
            .appComponent
            .noteDetailsComponent()
            .create(args.note?.id, args.draftState)
            .inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
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

    fun getDraftStateForTransfer(): NoteDetailsDraftState {
        return viewModel.uiState.value.toDraftState(args.note)
    }
}
