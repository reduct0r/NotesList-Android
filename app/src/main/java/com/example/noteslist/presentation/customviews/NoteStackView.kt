package com.example.noteslist.presentation.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
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

    private var stackSpacing: Int = 20.dpToPx()
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
            stackSpacing = getDimensionPixelSize(R.styleable.NoteStackView_stackSpacing, 20.dpToPx())
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

    private fun createCollapseButton(): TextView {
        return TextView(context).apply {
            text = context.getString(R.string.collapse)
            textSize = 15f
            setTextColor(context.getColor(R.color.status_read))
            gravity = Gravity.CENTER
            setPadding(16.dpToPx(), 12.dpToPx(), 16.dpToPx(), 12.dpToPx())
            setBackgroundColor(context.getColor(R.color.read_background))
            elevation = 4f
            setOnClickListener { isExpanded = false }
        }
    }

    private fun createNoteView(note: Note): NoteView {
        return NoteView(context).apply {
            title = note.title
            content = note.content
            time = note.getTimeString()
            isImportant = note.isImportant
            isRead = note.isRead
        }
    }

    override fun onInterceptTouchEvent(ev: android.view.MotionEvent?): Boolean {
        return !isExpanded || super.onInterceptTouchEvent(ev)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val parentWidth = MeasureSpec.getSize(widthMeasureSpec)
        val childWidthSpec = MeasureSpec.makeMeasureSpec(parentWidth, MeasureSpec.EXACTLY)

        var totalHeight = 0
        val childrenToMeasure = if (isExpanded) {
            noteViews + listOfNotNull(collapseButton)
        } else {
            noteViews.take(stackMaxVisible)
        }

        childrenToMeasure.forEach { child ->
            if (child.visibility == GONE) return@forEach
            child.measure(childWidthSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))

            if (isExpanded) {
                totalHeight += child.measuredHeight + 8.dpToPx()
            }
        }

        if (childrenToMeasure.isNotEmpty() && !isExpanded) {
            totalHeight = childrenToMeasure[0].measuredHeight + (childrenToMeasure.size - 1) * stackSpacing
        }

        setMeasuredDimension(parentWidth, resolveSize(totalHeight, heightMeasureSpec))
    }

    override fun onLayout(
        changed: Boolean,
        l: Int,
        t: Int,
        r: Int,
        b: Int
    ) {
        val parentWidth = r - l
        var currentTop = 0

        if (isExpanded) {
            noteViews.forEach { child ->
                if (child.visibility == VISIBLE) {
                    val h = child.measuredHeight
                    child.layout(0, currentTop, parentWidth, currentTop + h)
                    currentTop += h + 8.dpToPx()
                }
            }
            collapseButton?.let { btn ->
                if (btn.visibility == VISIBLE) {
                    btn.layout(0, currentTop, parentWidth, currentTop + btn.measuredHeight)
                }
            }
        } else {
            val visibleCount = minOf(stackMaxVisible, noteViews.size)
            val visibleNotes = noteViews.take(visibleCount)
            visibleNotes.forEachIndexed { index, child ->
                if (child.visibility == VISIBLE) {
                    val h = child.measuredHeight
                    val topOffset = index * stackSpacing
                    child.layout(0, topOffset, parentWidth, topOffset + h)
                    child.elevation = (visibleCount - index) * 6f
                }
            }
            visibleNotes.reversed().forEach { it.bringToFront() }
        }
    }

    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()
}

