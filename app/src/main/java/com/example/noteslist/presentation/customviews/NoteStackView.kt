package com.example.noteslist.presentation.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.TextView
import com.example.noteslist.model.Note

class NoteStackView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    private var stackSpacing: Int = 20
    private var stackMaxVisible: Int = 3
    private var isExpanded = false
        set(value) {
            field = value
            updateVisibilityAndButton()
            requestLayout()
        }
    private val noteViews = mutableListOf<NoteView>()
    private var collapseButton: TextView? = null


    private fun updateVisibilityAndButton() {
        TODO("Not yet implemented")
    }

    override fun onLayout(
        changed: Boolean,
        l: Int,
        t: Int,
        r: Int,
        b: Int
    ) {
        TODO("Not yet implemented")
    }

    fun setNotes(notes: List<Note>) {
        TODO("Not yet implemented")
    }

    private fun createNoteView(note: Note): NoteView {
        TODO("Not yet implemented")
    }
}

