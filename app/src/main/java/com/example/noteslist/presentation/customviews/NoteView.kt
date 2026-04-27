package com.example.noteslist.presentation.customviews

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewOutlineProvider
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.withStyledAttributes
import androidx.core.view.isVisible
import com.example.noteslist.R
import com.example.noteslist.databinding.NoteViewBinding

class NoteView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr, defStyleRes) {

    private val binding = NoteViewBinding.inflate(LayoutInflater.from(context), this, true)

    var isRead: Boolean = false
        set(value) {
            field = value
            updateBackgroundAndStatus()
        }

    var isImportant: Boolean = false
        set(value) {
            field = value
            binding.ivImportant.isVisible = value
        }

    var title: String = ""
        set(value) {
            field = value
            binding.tvTitle.text = value
        }

    var content: String = ""
        set(value) {
            field = value
            binding.tvContent.text = value
            applyFadeToSecondLine()
        }

    var time: String = ""
        set(value) {
            field = value
            binding.tvTime.text = value
        }

    private val backgroundDrawable = GradientDrawable().apply {
        shape = GradientDrawable.RECTANGLE
    }

    private var unreadBackgroundColor: Int = 0

    init {
        clipToOutline = false
        context.withStyledAttributes(attrs, R.styleable.NoteView, defStyleAttr, defStyleRes) {
            unreadBackgroundColor = getColor(
                R.styleable.NoteView_noteBackgroundColor,
                context.getColor(R.color.unread_background)
            )

            val textColor = getColor(
                R.styleable.NoteView_noteTextColor,
                context.getColor(R.color.text_primary)
            )

            val cornerRadius = getDimension(
                R.styleable.NoteView_noteCornerRadius,
                DEFAULT_CORNER_RADIUS
            )

            val elev = getDimension(
                R.styleable.NoteView_noteElevation,
                DEFAULT_ELEVATION
            )

            backgroundDrawable.cornerRadius = cornerRadius
            backgroundDrawable.setColor(unreadBackgroundColor)
            background = backgroundDrawable

            elevation = elev
            clipToOutline = true
            outlineProvider = ViewOutlineProvider.BACKGROUND

            binding.tvTitle.setTextColor(textColor)
            binding.tvContent.setTextColor(textColor)
            binding.tvTime.setTextColor(textColor)
        }

        updateBackgroundAndStatus()
        applyFadeToSecondLine()
    }

    private fun updateBackgroundAndStatus() {
        backgroundDrawable.setColor(if (isRead) context.getColor(R.color.read_background) else unreadBackgroundColor)

        binding.tvStatus.apply {
            text = context.getString(if (isRead) R.string.note_read else R.string.note_unread)
            setTextColor(context.getColor(if (isRead) R.color.status_read else R.color.status_unread))
        }

        applyFadeToSecondLine()
    }

    private fun applyFadeToSecondLine() {
        binding.tvContent.post {
            if (binding.tvContent.lineCount < 2) {
                binding.fadeOverlay.isVisible = false
                return@post
            }

            binding.fadeOverlay.apply {
                isVisible = binding.tvContent.lineCount >= 2
                translationY = binding.tvContent.lineHeight * FADE_TRANSLATION_RATIO
                layoutParams.height = binding.tvContent.lineHeight
                background = GradientDrawable(
                    GradientDrawable.Orientation.LEFT_RIGHT,
                    intArrayOf(
                        Color.TRANSPARENT,
                        Color.TRANSPARENT,
                        Color.TRANSPARENT
                    )
                )
            }
        }
    }

    private companion object {
        private const val DEFAULT_CORNER_RADIUS = 900f
        private const val DEFAULT_ELEVATION = 8f
        private const val FADE_TRANSLATION_RATIO = 0.8f
    }
}