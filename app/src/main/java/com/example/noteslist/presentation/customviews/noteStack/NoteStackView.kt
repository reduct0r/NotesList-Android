package com.example.noteslist.presentation.customviews.noteStack

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.withStyledAttributes
import androidx.core.view.isVisible
import com.example.noteslist.R
import com.example.noteslist.domain.model.Note
import com.example.noteslist.domain.model.getTimeString
import com.example.noteslist.presentation.customviews.note.NoteView
import com.example.noteslist.presentation.notesList.StackSettings
import kotlin.math.roundToInt

class NoteStackView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    private var stackSpacingPx: Int = DEFAULT_STACK_SPACING_DP.dpToPx()
    private var expandedSpacingPx: Int = DEFAULT_EXPANDED_SPACING_DP.dpToPx()
    private var stackMaxVisible: Int = DEFAULT_MAX_VISIBLE
    private var elementsElevation: Int = DEFAULT_ELEVATION.dpToPx()

    private val noteViews = mutableListOf<NoteView>()
    private var collapseButton: TextView? = null
    private var pendingExpandAnimation = false

    var onToggleRequest: (() -> Unit)? = null

    private val expandAnimator = StackExpandAnimator()

    init {
        clipChildren = false
        clipToPadding = false

        context.withStyledAttributes(attrs, R.styleable.NoteStackView, defStyleAttr, 0) {
            stackSpacingPx = getDimensionPixelSize(
                R.styleable.NoteStackView_stackSpacing,
                DEFAULT_STACK_SPACING_DP.dpToPx()
            )
            expandedSpacingPx = getDimensionPixelSize(
                R.styleable.NoteStackView_stackElOffset,
                DEFAULT_EXPANDED_SPACING_DP.dpToPx()
            )
            stackMaxVisible = getInt(R.styleable.NoteStackView_stackMaxVisible, DEFAULT_MAX_VISIBLE)
            elementsElevation = getDimensionPixelSize(R.styleable.NoteStackView_elementsElevation, DEFAULT_ELEVATION.dpToPx())
        }
    }

    fun getStackSettings(): StackSettings {
        return StackSettings(
            stackSpacing = stackSpacingPx.pxToDp(),
            stackMaxVisible = stackMaxVisible
        )
    }

    fun applySettings(settings: StackSettings) {
        stackSpacingPx = settings.stackSpacing.coerceAtLeast(0).dpToPx()
        stackMaxVisible = settings.stackMaxVisible.coerceAtLeast(1)

        if (noteViews.isNotEmpty()) {
            updateVisibilityAndButton(
                isExpanded = isCurrentlyExpanded(),
                isSingleNote = isSingleNote()
            )
        }

        requestLayout()
        invalidate()
    }

    fun setNotes(
        notes: List<Note>,
        expanded: Boolean,
        shouldAnimateExpand: Boolean,
        onNoteClick: (Note) -> Unit,
        onNoteLongClick: (Note) -> Unit
    ) {
        val isSingleNote = notes.size == 1
        val shouldShowExpanded = expanded || isSingleNote
        val shouldRunExpandAnimation = shouldAnimateExpand && shouldShowExpanded && !isSingleNote

        removeAllViews()
        noteViews.clear()
        collapseButton = null
        pendingExpandAnimation = shouldRunExpandAnimation

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

        updateVisibilityAndButton(isExpanded = shouldShowExpanded, isSingleNote = isSingleNote)
        requestLayout()
    }

    private fun updateVisibilityAndButton(
        isExpanded: Boolean,
        isSingleNote: Boolean
    ) {
        isClickable = !isExpanded && !isSingleNote

        if (isExpanded || isSingleNote) {
            noteViews.forEach { it.isVisible = true }

            if (collapseButton == null && !isSingleNote) {
                collapseButton = createCollapseButton()
                addView(collapseButton)

                if (pendingExpandAnimation) {
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
            noteViews.forEachIndexed { index, noteView ->
                noteView.isVisible = index < stackMaxVisible
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
            setOnClickListener { onToggleRequest?.invoke() }
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
        return !isCurrentlyExpanded() && !isSingleNote()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (isCurrentlyExpanded() || isSingleNote()) return false
        if (event?.action == MotionEvent.ACTION_UP) {
            onToggleRequest?.invoke()
        }
        return true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val parentWidth = MeasureSpec.getSize(widthMeasureSpec)
        val childWidthSpec = MeasureSpec.makeMeasureSpec(parentWidth, MeasureSpec.EXACTLY)
        val isExpanded = isCurrentlyExpanded()
        val isSingleNote = isSingleNote()

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
            childrenToMeasure
                .filter { it.isVisible }
                .sumOf { it.measuredHeight } +
                (childrenToMeasure.count { it.isVisible } - 1).coerceAtLeast(0) * expandedSpacingPx
        } else if (childrenToMeasure.isNotEmpty()) {
            childrenToMeasure.first().measuredHeight +
                (childrenToMeasure.size - 1).coerceAtLeast(0) * stackSpacingPx
        } else {
            0
        }

        setMeasuredDimension(parentWidth, resolveSize(totalHeight, heightMeasureSpec))
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val parentWidth = r - l
        var currentTop = 0
        val isExpanded = isCurrentlyExpanded()
        val isSingleNote = isSingleNote()
        val collapsedTops = buildCollapsedTops()

        if (isExpanded || isSingleNote) {
            noteViews.forEach { child ->
                if (child.isVisible) {
                    val h = child.measuredHeight
                    child.layout(0, currentTop, parentWidth, currentTop + h)
                    currentTop += h + expandedSpacingPx
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
                    collapsedTops = collapsedTops,
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
                    val topOffset = collapsedTops[index]
                    child.layout(0, topOffset, parentWidth, topOffset + h)
                    child.elevation = (visibleCount - index) + elementsElevation.toFloat()
                }
            }
            visibleNotes.reversed().forEach { it.bringToFront() }
        }
    }

    private fun buildCollapsedTops(): List<Int> {
        if (noteViews.isEmpty()) return emptyList()

        val visibleCount = minOf(stackMaxVisible, noteViews.size)
        val collapsedTops = MutableList(noteViews.size) { 0 }

        for (index in 0 until visibleCount) {
            collapsedTops[index] = index * stackSpacingPx
        }

        val lastVisibleTop = collapsedTops[visibleCount - 1]
        for (index in visibleCount until noteViews.size) {
            collapsedTops[index] = lastVisibleTop
        }

        return collapsedTops
    }

    private fun isSingleNote(): Boolean = noteViews.size == 1

    private fun isCurrentlyExpanded(): Boolean = collapseButton != null || isSingleNote()

    private fun Int.dpToPx(): Int = (this * resources.displayMetrics.density).toInt()

    private fun Int.pxToDp(): Int = (this / resources.displayMetrics.density).roundToInt()

    companion object {
        private const val DEFAULT_STACK_SPACING_DP = 8
        private const val DEFAULT_EXPANDED_SPACING_DP = 20
        private const val DEFAULT_MAX_VISIBLE = 3
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
