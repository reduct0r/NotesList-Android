package com.example.noteslist.presentation.customviews

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewOutlineProvider
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.withStyledAttributes
import com.example.noteslist.R
import com.example.noteslist.databinding.NoteViewBinding

class NoteView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): ConstraintLayout(context, attrs, defStyleAttr) {
    private var binding: NoteViewBinding =
        NoteViewBinding.inflate(LayoutInflater.from(context), this, true)

    var isRead: Boolean = false
        set(value) {
            field = value
            updateBackgroundAndStatus()
        }

    var isImportant: Boolean = false
        set(value) {
            field = value
            binding.ivImportant.visibility = if (value) VISIBLE else GONE
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

        context.withStyledAttributes(attrs, R.styleable.NoteView, defStyleAttr, 0) {

            unreadBackgroundColor = getColor(
                R.styleable.NoteView_noteBackgroundColor,
                context.getColor(R.color.unread_background)
            )

            val bgColor = getColor(
                R.styleable.NoteView_noteBackgroundColor,
                context.getColor(R.color.unread_background)
            )

            val textColor = getColor(
                R.styleable.NoteView_noteTextColor,
                context.getColor(R.color.text_primary)
            )

            val cornerRadius = getDimension(
                R.styleable.NoteView_noteCornerRadius,
                12f * resources.displayMetrics.density
            )

            val elev = getDimension(
                R.styleable.NoteView_noteElevation,
                8f * resources.displayMetrics.density
            )

        this@NoteView.apply {
            unreadBackgroundColor = bgColor

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
    }
        updateBackgroundAndStatus()
        applyFadeToSecondLine()
    }

    private fun updateBackgroundAndStatus() {
        val newColor = if (isRead) {
            context.getColor(R.color.read_background)
        } else {
            unreadBackgroundColor
        }
        backgroundDrawable.setColor(newColor)

        if (isRead) {
            binding.tvStatus.text = context.getString(R.string.note_read)
            binding.tvStatus.setTextColor(context.getColor(R.color.status_read))
        } else {
            binding.tvStatus.text = context.getString(R.string.note_unread)
            binding.tvStatus.setTextColor(context.getColor(R.color.status_unread))
        }
        applyFadeToSecondLine()
    }

    private fun applyFadeToSecondLine() {
        binding.tvContent.post {
            if (binding.tvContent.lineCount < 2) {
                binding.fadeOverlay.visibility = GONE
                return@post
            }

            binding.fadeOverlay.apply {
                visibility = VISIBLE
                translationY = binding.tvContent.lineHeight.toFloat() * 0.8f
                layoutParams.height = binding.tvContent.lineHeight

                val gradient = GradientDrawable(
                    GradientDrawable.Orientation.LEFT_RIGHT,
                    intArrayOf(
                        Color.TRANSPARENT,
                        Color.TRANSPARENT,
                        backgroundDrawable.color?.defaultColor ?: unreadBackgroundColor
                    )
                )

                background = gradient
            }
        }
    }
}