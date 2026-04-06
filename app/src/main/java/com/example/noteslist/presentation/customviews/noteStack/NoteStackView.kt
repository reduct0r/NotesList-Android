package com.example.noteslist.presentation.customviews.noteStack

import android.content.Context
import android.graphics.drawable.GradientDrawable
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
import com.example.noteslist.presentation.customviews.note.NoteView

class NoteStackView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    private var childSpacing: Int = DEFAULT_CHILD_SPACING_DP.dpToPx()
    private var stackMaxVisible: Int = DEFAULT_MAX_VISIBLE
    private var stackElementOffset: Int = DEFAULT_STACK_OFFSET_DP.dpToPx()
    private var elementsElevation: Int = DEFAULT_ELEVATION.dpToPx()

    private val noteViews = mutableListOf<NoteView>()
    private var collapseButton: TextView? = null
    private var shouldAnimateNextExpand = false
    private var pendingExpandAnimation = false

    private var isExpanded = false
        set(value) {
            val wasExpanded = field
            field = value

            val isStartingExpandAnimation = value && !wasExpanded && !isSingleNote && shouldAnimateNextExpand
            pendingExpandAnimation = isStartingExpandAnimation

            updateVisibilityAndButton(isStartingExpandAnimation)
            requestLayout()

            shouldAnimateNextExpand = false
        }

    private var isSingleNote = false

    var onExpandRequest: (() -> Unit)? = null
    var onCollapseRequest: (() -> Unit)? = null

    private val expandAnimator = StackExpandAnimator()

    init {
        clipChildren = false
        clipToPadding = false

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
        shouldAnimateExpand: Boolean,
        onNoteClick: (Note) -> Unit,
        onNoteLongClick: (Note) -> Unit
    ) {
        isSingleNote = notes.size == 1

        val targetExpanded = expanded || notes.size == 1
        shouldAnimateNextExpand = (shouldAnimateExpand) &&
                !isExpanded &&
                targetExpanded &&
                notes.size > 1

        if (isExpanded && targetExpanded && noteViews.size == notes.size) {
            updateExistingNoteViews(notes, onNoteClick, onNoteLongClick)
            return
        }

        removeAllViews()
        noteViews.clear()
        collapseButton = null

        val sorted = notes.sortedByDescending { it.createdAt }

        sorted.forEach { note ->
            createNoteView(note).also { noteView ->
                noteViews.add(noteView)
                addView(noteView)
                noteView.setOnClickListener { onNoteClick(note) }
                noteView.setOnLongClickListener {
                    onNoteLongClick(note)
                    true
                }
            }
        }

        isSingleNote = sorted.size == 1
        isExpanded = targetExpanded
    }

    private fun updateExistingNoteViews(
        newNotes: List<Note>,
        onNoteClick: (Note) -> Unit,
        onNoteLongClick: (Note) -> Unit
    ) {
        val sorted = newNotes.sortedByDescending { it.createdAt }
        noteViews.forEachIndexed { index, noteView ->
            val note = sorted[index]
            noteView.apply {
                title = note.title
                content = note.content
                time = note.getTimeString()
                isImportant = note.isImportant
                isRead = note.isRead
                setOnClickListener { onNoteClick(note) }
                setOnLongClickListener {
                    onNoteLongClick(note)
                    true
                }
            }
        }
    }

    private fun updateVisibilityAndButton(startingExpandAnimation: Boolean = false) {
        isClickable = !isExpanded && !isSingleNote

        if (isExpanded || isSingleNote) {
            noteViews.forEach { it.isVisible = true }

            if (collapseButton == null && !isSingleNote) {
                collapseButton = createCollapseButton()
                addView(collapseButton)

                if (startingExpandAnimation) {
                    collapseButton?.apply {
                        alpha = COLLAPSE_BUTTON_HIDDEN_ALPHA
                        scaleX = COLLAPSE_BUTTON_HIDDEN_SCALE
                        scaleY = COLLAPSE_BUTTON_HIDDEN_SCALE
                    }
                } else {
                    collapseButton?.apply {
                        alpha = COLLAPSE_BUTTON_VISIBLE_ALPHA
                        scaleX = COLLAPSE_BUTTON_VISIBLE_SCALE
                        scaleY = COLLAPSE_BUTTON_VISIBLE_SCALE
                    }
                }
            }
        } else {
            for (i in stackMaxVisible until noteViews.size) {
                noteViews[i].isGone = true
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
            textSize = COLLAPSE_TEXT_SIZE_SP
            setTextColor(context.getColor(R.color.status_read))
            gravity = Gravity.CENTER
            setPadding(
                COLLAPSE_PADDING_HORIZONTAL.dpToPx(),
                COLLAPSE_PADDING_VERTICAL.dpToPx(),
                COLLAPSE_PADDING_HORIZONTAL.dpToPx(),
                COLLAPSE_PADDING_VERTICAL.dpToPx()
            )
            background = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = DEFAULT_BUTTON_RADIUS.dpToPx().toFloat()
                setColor(context.getColor(R.color.read_background))
            }
            elevation = elementsElevation.toFloat()
            setOnClickListener { onCollapseRequest?.invoke() }
        }
    }

    private fun createNoteView(note: Note): NoteView {
        return NoteView(context, null, 0, R.style.NoteStyle_NotRead).apply {
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
        if (isExpanded || isSingleNote) return false
        if (event?.action == MotionEvent.ACTION_UP) {
            onExpandRequest?.invoke()
        }
        return true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val parentWidth = MeasureSpec.getSize(widthMeasureSpec)
        val childWidthSpec = MeasureSpec.makeMeasureSpec(parentWidth, MeasureSpec.EXACTLY)

        val childrenToMeasure = if (isExpanded || isSingleNote) {
            noteViews + listOfNotNull(collapseButton)
        } else {
            noteViews.take(stackMaxVisible)
        }

        childrenToMeasure.forEach { child ->
            if (child.isVisible) {
                child.measure(childWidthSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED))
            }
        }

        val totalHeight = if (isExpanded || isSingleNote) {
            childrenToMeasure.filter { it.isVisible }
                .sumOf { it.measuredHeight + childSpacing }
        } else if (childrenToMeasure.isNotEmpty()) {
            childrenToMeasure[0].measuredHeight + (childrenToMeasure.size - 1) * stackElementOffset
        } else {
            0
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

            if (pendingExpandAnimation) {
                pendingExpandAnimation = false
                expandAnimator.startExpandAnimation(
                    noteViews = noteViews,
                    collapseButton = collapseButton,
                    stackElementOffset = stackElementOffset,
                    elementsElevation = elementsElevation,
                    parentForPost = this
                )
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

        private const val COLLAPSE_BUTTON_HIDDEN_ALPHA = 0f
        private const val COLLAPSE_BUTTON_VISIBLE_ALPHA = 1f
        private const val COLLAPSE_BUTTON_HIDDEN_SCALE = 0.7f
        private const val COLLAPSE_BUTTON_VISIBLE_SCALE = 1f

        private const val COLLAPSE_TEXT_SIZE_SP = 15f
        private const val COLLAPSE_PADDING_HORIZONTAL = 16
        private const val COLLAPSE_PADDING_VERTICAL = 12

        private const val DEFAULT_BUTTON_RADIUS = 12
    }
}