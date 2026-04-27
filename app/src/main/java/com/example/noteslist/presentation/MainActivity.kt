package com.example.noteslist.presentation

import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import android.os.Bundle
import android.widget.Toast
import androidx.core.os.BundleCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.navigation.fragment.NavHostFragment
import com.example.noteslist.NoteListApp
import com.example.noteslist.R
import com.example.noteslist.databinding.ActivityMainBinding
import com.example.noteslist.domain.common.AppClock
import com.example.noteslist.domain.model.Note
import com.example.noteslist.presentation.noteDetails.NoteDetailsFragment
import com.example.noteslist.presentation.noteDetails.NoteDetailsFragmentArgs
import com.example.noteslist.presentation.noteDetails.NoteDetailsDraftState
import com.example.noteslist.presentation.notesList.NoteListFragmentDirections
import javax.inject.Inject


class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var appClock: AppClock

    private lateinit var binding: ActivityMainBinding
    private var lastBackPressedAt = 0L
    private var shouldShowExitDialogAfterPaneClose = false
    private var pendingSinglePaneDraftState: NoteDetailsDraftState? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        (applicationContext as NoteListApp).appComponent.inject(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pendingSinglePaneDraftState = savedInstanceState?.let {
            BundleCompat.getParcelable(
                it,
                KEY_PENDING_SINGLE_PANE_DRAFT_STATE,
                NoteDetailsDraftState::class.java
            )
        }

        window.decorView.post {
            if (isTwoPaneMode()) {
                syncTwoPaneStateIfNeeded()
            } else {
                syncSinglePaneStateIfNeeded()
            }
        }

        onBackPressedDispatcher.addCallback(this,
            object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (isTwoPaneMode()) {
                    if (isNoteDetailsPaneOpened()) {
                        isEnabled = false
                        onBackPressedDispatcher.onBackPressed()
                        isEnabled = true

                        if (!isNoteDetailsPaneOpened()) {
                            shouldShowExitDialogAfterPaneClose = true
                        }
                        return
                    }

                    if (shouldShowExitDialogAfterPaneClose) {
                        shouldShowExitDialogAfterPaneClose = false
                        showExitConfirmationDialog()
                        return
                    }

                    showExitConfirmationByDoubleBack()
                    return
                }

                val navHost = supportFragmentManager
                    .findFragmentById(binding.navHostFragment.id) as? NavHostFragment
                val navController = navHost?.navController
                val currentDestinationId = navController?.currentDestination?.id

                if (currentDestinationId != R.id.noteListFragment) {
                    isEnabled = false
                    onBackPressedDispatcher.onBackPressed()
                    isEnabled = true
                    return
                }

                showExitConfirmationByDoubleBack()
            }
        })
    }

    fun isTwoPaneMode(): Boolean {
        return binding.detailContainer != null
    }

    fun openNoteDetailsPane(note: Note?, draftState: NoteDetailsDraftState? = null) {
        if (!isTwoPaneMode()) return
        shouldShowExitDialogAfterPaneClose = false

        val fragment = NoteDetailsFragment().apply {
            arguments = NoteDetailsFragmentArgs(note, draftState).toBundle()
        }

        supportFragmentManager.commit {
            setReorderingAllowed(true)
            replace(binding.detailContainer?.id ?: return, fragment, NOTE_DETAILS_TAG)
        }
    }

    fun closeNoteDetailsPane() {
        if (!isTwoPaneMode()) return

        supportFragmentManager.findFragmentById(binding.detailContainer?.id ?: return)?.let { fragment ->
            supportFragmentManager.commit {
                setReorderingAllowed(true)
                remove(fragment)
            }
        }
    }

    private fun isNoteDetailsPaneOpened(): Boolean {
        return isTwoPaneMode() && supportFragmentManager
            .findFragmentById(binding.detailContainer?.id ?: return false) != null
    }

    private fun syncTwoPaneStateIfNeeded() {
        if (!isTwoPaneMode()) return

        val navHost = supportFragmentManager
            .findFragmentById(binding.navHostFragment.id) as? NavHostFragment
        val navController = navHost?.navController ?: return

        if (navController.currentDestination?.id != R.id.noteDetailsFragment) return

        val detailsFragment = navHost.childFragmentManager.fragments
            .filterIsInstance<NoteDetailsFragment>()
            .firstOrNull()

        val draftState = detailsFragment?.getDraftStateForTransfer() ?: return

        navController.popBackStack(R.id.noteListFragment, false)
        openNoteDetailsPane(draftState.originalNote, draftState)
    }

    private fun syncSinglePaneStateIfNeeded() {
        if (isTwoPaneMode()) return

        val draftState = pendingSinglePaneDraftState ?: return
        val navHost = supportFragmentManager
            .findFragmentById(binding.navHostFragment.id) as? NavHostFragment
        val navController = navHost?.navController ?: return

        if (navController.currentDestination?.id == R.id.noteListFragment) {
            val direction = NoteListFragmentDirections
                .actionNoteListFragmentToNoteDetailsFragment(
                    draftState.originalNote,
                    draftState
                )
            navController.navigate(direction)
        }
        pendingSinglePaneDraftState = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        if (isTwoPaneMode()) {
            val detailsFragment = supportFragmentManager
                .findFragmentById(binding.detailContainer?.id ?: -1) as? NoteDetailsFragment
            val draftState = detailsFragment?.getDraftStateForTransfer()
            outState.putParcelable(KEY_PENDING_SINGLE_PANE_DRAFT_STATE, draftState)
        }
        super.onSaveInstanceState(outState)
    }

    private fun showExitConfirmationByDoubleBack() {
        val now = appClock.elapsedRealtime()
        val isSecondBackClick = now - lastBackPressedAt <= BACK_PRESS_WINDOW_MS
        lastBackPressedAt = now

        if (isSecondBackClick) {
            showExitConfirmationDialog()
        } else {
            Toast.makeText(
                this@MainActivity,
                "Нажмите назад ещё раз для выхода",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun showExitConfirmationDialog() {
        AlertDialog.Builder(this@MainActivity)
            .setTitle("Выход")
            .setMessage("Закрыть приложение?")
            .setPositiveButton("Да") { _, _ -> finish() }
            .setNegativeButton("Нет", null)
            .show()
    }

    companion object {
        private const val BACK_PRESS_WINDOW_MS = 2000L
        private const val NOTE_DETAILS_TAG = "note_details_pane"
        private const val KEY_PENDING_SINGLE_PANE_DRAFT_STATE =
            "key_pending_single_pane_draft_state"
    }
}
