package com.example.noteslist.presentation.customviews

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import com.example.noteslist.R
import com.example.noteslist.databinding.NoteViewBinding

class NoteView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
): FrameLayout(context, attrs, defStyleAttr) {
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

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.NoteView, defStyleAttr, 0)

        unreadBackgroundColor = typedArray.getColor(
            R.styleable.NoteView_noteBackgroundColor,
            context.getColor(R.color.unread_background)
        )

        val textColor = typedArray.getColor(
            R.styleable.NoteView_noteTextColor,
            context.getColor(R.color.text_primary)
        )

        val cornerRadius = typedArray.getDimension(
            R.styleable.NoteView_noteCornerRadius, 12f * resources.displayMetrics.density
        )

        val elevation = typedArray.getDimension(
            R.styleable.NoteView_noteElevation, 8f * resources.displayMetrics.density
        )

        typedArray.recycle()

        backgroundDrawable.cornerRadius = cornerRadius
        backgroundDrawable.setColor(unreadBackgroundColor)
        background = backgroundDrawable

        this.elevation = elevation
        this.clipToOutline = true
        this.outlineProvider = ViewOutlineProvider.BACKGROUND

        binding.tvTitle.setTextColor(textColor)
        binding.tvContent.setTextColor(textColor)
        binding.tvTime.setTextColor(textColor)

        setOnClickListener {
            isRead = !isRead
        }

        updateBackgroundAndStatus()
    }

    private fun updateBackgroundAndStatus() {
        TODO("Not yet implemented")
    }
}