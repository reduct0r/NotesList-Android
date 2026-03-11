package com.example.noteslist.presentation.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.noteslist.R
import com.example.noteslist.domain.model.Note
import androidx.core.content.withStyledAttributes

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

    init {
        context.withStyledAttributes(attrs, R.styleable.NoteStackView, defStyleAttr, 0) {
            stackSpacing = getDimensionPixelSize(R.styleable.NoteStackView_stackSpacing, 20)
            stackMaxVisible = getInt(R.styleable.NoteStackView_stackMaxVisible, 3)
        }

        setOnClickListener {
            if (!isExpanded && noteViews.isNotEmpty()) isExpanded = true
        }
    }

    fun setNotes(notes: List<Note>) {
        removeAllViews()
        noteViews.clear()

        val sorted = notes.sortedByDescending { it.createdAt }

        sorted.forEach { note ->
            val noteView = createNoteView(note)
            noteViews.add(noteView)
            addView(noteView)
        }

        updateVisibilityAndButton()
        requestLayout()
    }

    private fun updateVisibilityAndButton() {
        if (isExpanded) {
            noteViews.forEach { it.visibility = VISIBLE }
            if (collapseButton == null) {
                collapseButton = createCollapseButton()
                addView(collapseButton)
            }
        } else {
            for (i in stackMaxVisible until noteViews.size) {
                noteViews[i].visibility = GONE
            }
            collapseButton?.let {
                removeView(it)
                collapseButton = null
            }
        }
    }

    private fun createCollapseButton(): TextView? {
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

    private fun createNoteView(note: Note): NoteView {
        TODO("Not yet implemented")
    }
}

