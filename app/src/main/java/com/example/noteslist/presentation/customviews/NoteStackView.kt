package com.example.noteslist.presentation.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.withStyledAttributes
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.example.noteslist.R
import com.example.noteslist.domain.model.Note
import com.example.noteslist.domain.model.getTimeString

class NoteStackView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    private var childSpacing: Int = DEFAULT_CHILD_SPACING_DP.dpToPx()
    private var stackMaxVisible: Int = DEFAULT_MAX_VISIBLE
    private var stackElementOffset: Int = DEFAULT_STACK_OFFSET_DP.dpToPx()
    private var elementsElevation: Int = DEFAULT_ELEVATION.dpToPx()

    private var isExpanded = false
        set(value) {
            field = value
            updateVisibilityAndButton()
            requestLayout()
        }

    private var isSingleNote = false
    private val noteViews = mutableListOf<NoteView>()
    private var collapseButton: TextView? = null
    var onExpandRequest: (() -> Unit)? = null
    var onCollapseRequest: (() -> Unit)? = null

    init {
        context.withStyledAttributes(attrs, R.styleable.NoteStackView, defStyleAttr, 0) {
            childSpacing = getDimensionPixelSize(R.styleable.NoteStackView_stackSpacing, DEFAULT_CHILD_SPACING_DP.dpToPx())
            stackMaxVisible = getInt(R.styleable.NoteStackView_stackMaxVisible, DEFAULT_MAX_VISIBLE)
            stackElementOffset = getDimensionPixelSize(R.styleable.NoteStackView_stackElOffset, DEFAULT_STACK_OFFSET_DP.dpToPx())
            elementsElevation = getDimensionPixelSize(R.styleable.NoteStackView_elementsElevation, DEFAULT_ELEVATION.dpToPx())
        }
    }

    fun setNotes(
        notes: List<Note>,
        expanded: Boolean,
        onNoteClick: (Note) -> Unit
    ) {
        removeAllViews()
        noteViews.clear()
        collapseButton = null

        val sorted = notes.sortedByDescending { it.createdAt }

        sorted.forEach { note ->
            val noteView = createNoteView(note)
            noteViews.add(noteView)
            addView(noteView)

            noteView.setOnClickListener { onNoteClick(note) }
        }

        isSingleNote = sorted.size == 1
        isExpanded = expanded || isSingleNote
    }

    private fun updateVisibilityAndButton() {
        isClickable = !isExpanded && !isSingleNote

        if (isExpanded || isSingleNote) {
            noteViews.forEach { it.visibility = VISIBLE }

            if (collapseButton == null && !isSingleNote) {
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
            elevation = elementsElevation.toFloat()
            setOnClickListener { onCollapseRequest?.invoke() }
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


    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return !isExpanded && !isSingleNote
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (isExpanded || isSingleNote) {
            return false
        }
        if (event?.action == MotionEvent.ACTION_UP) {
            onExpandRequest?.invoke()
        }
        return true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val parentWidth = MeasureSpec.getSize(widthMeasureSpec)
        val childWidthSpec = MeasureSpec.makeMeasureSpec(parentWidth, MeasureSpec.EXACTLY)

        var totalHeight = 0
        val childrenToMeasure = if (isExpanded || isSingleNote) {
            noteViews + listOfNotNull(collapseButton)
        } else {
            noteViews.take(stackMaxVisible)
        }

        childrenToMeasure.forEach { child ->
            if (child.isGone) return@forEach
            child.measure(childWidthSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))

            if (isExpanded || isSingleNote) {
                totalHeight += child.measuredHeight + childSpacing
            }
        }

        if (childrenToMeasure.isNotEmpty() && !isExpanded && !isSingleNote) {
            totalHeight = childrenToMeasure[0].measuredHeight + (childrenToMeasure.size - 1) * stackElementOffset
        }

        setMeasuredDimension(parentWidth, resolveSize(totalHeight, heightMeasureSpec))
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val parentWidth = r - l
        var currentTop = 0

        if (isExpanded || isSingleNote) {
            noteViews.forEach { child ->
                if (child.isVisible) {
                    val h = child.measuredHeight
                    child.layout(0, currentTop, parentWidth, currentTop + h)
                    currentTop += h + childSpacing
                }
            }
            collapseButton?.let { btn ->
                if (btn.isVisible) {
                    btn.layout(0, currentTop, parentWidth, currentTop + btn.measuredHeight)
                }
            }
        } else {
            val visibleCount = minOf(stackMaxVisible, noteViews.size)
            val visibleNotes = noteViews.take(visibleCount)
            visibleNotes.forEachIndexed { index, child ->
                if (child.isVisible) {
                    val h = child.measuredHeight
                    val topOffset = index * stackElementOffset
                    child.layout(0, topOffset, parentWidth, topOffset + h)
                    child.elevation = (visibleCount - index) + elementsElevation.toFloat()
                }
            }
            visibleNotes.reversed().forEach { it.bringToFront() }
        }
    }

    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()

    companion object {
        private const val DEFAULT_CHILD_SPACING_DP = 8
        private const val DEFAULT_MAX_VISIBLE = 3
        private const val DEFAULT_STACK_OFFSET_DP = 20
        private const val DEFAULT_ELEVATION = 4
    }
}