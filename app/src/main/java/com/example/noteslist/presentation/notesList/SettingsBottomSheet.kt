package com.example.noteslist.presentation.notesList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SettingsBottomSheet : BottomSheetDialogFragment() {

    private val viewModel: NoteListViewModel by activityViewModels {
        (requireParentFragment() as NoteListFragment).viewModelFactory
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val stackSettings by viewModel.stackSettings.collectAsState()

                MaterialTheme {
                    SettingsBottomSheetContent(
                        stackSettings = stackSettings,
                        onApply = { stackSpacing, stackMaxVisible ->
                            viewModel.updateStackSettings(stackSpacing, stackMaxVisible)
                            dismiss()
                        }
                    )
                }
            }
        }
    }
}
