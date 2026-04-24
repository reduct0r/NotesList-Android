package com.example.noteslist.presentation

import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import android.os.Bundle
import android.os.SystemClock
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.navigation.fragment.NavHostFragment
import com.example.noteslist.R
import com.example.noteslist.databinding.ActivityMainBinding
import com.example.noteslist.domain.model.Note
import com.example.noteslist.presentation.noteDetails.NoteDetailsFragment
import com.example.noteslist.presentation.noteDetails.NoteDetailsFragmentArgs


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private var lastBackPressedAt = 0L
    private var shouldShowExitDialogAfterPaneClose = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.decorView.post {
            syncTwoPaneStateIfNeeded()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
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

    fun openNoteDetailsPane(note: Note?) {
        if (!isTwoPaneMode()) return
        shouldShowExitDialogAfterPaneClose = false

        val fragment = NoteDetailsFragment().apply {
            arguments = NoteDetailsFragmentArgs(note).toBundle()
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
        return isTwoPaneMode() && supportFragmentManager.findFragmentById(binding.detailContainer?.id ?: return false) != null
    }

    private fun syncTwoPaneStateIfNeeded() {
        if (!isTwoPaneMode()) return

        val navHost = supportFragmentManager.findFragmentById(binding.navHostFragment.id) as? NavHostFragment
        val navController = navHost?.navController ?: return

        if (navController.currentDestination?.id != R.id.noteDetailsFragment) return

        val detailsFragment = navHost.childFragmentManager.fragments
            .filterIsInstance<NoteDetailsFragment>()
            .firstOrNull()

        val note = detailsFragment?.getCurrentNoteForTransfer()

        navController.popBackStack(R.id.noteListFragment, false)
        openNoteDetailsPane(note)
    }

    private fun showExitConfirmationByDoubleBack() {
        val now = SystemClock.elapsedRealtime()
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
    }
}
