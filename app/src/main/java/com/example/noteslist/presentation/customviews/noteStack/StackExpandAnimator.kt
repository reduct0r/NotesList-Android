package com.example.noteslist.presentation.customviews.noteStack

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.PathInterpolator
import android.widget.TextView
import com.example.noteslist.presentation.customviews.note.NoteView

class StackExpandAnimator {

    private val interpolator = PathInterpolator(
        BEZIER_X1,
        BEZIER_Y1,
        BEZIER_X2,
        BEZIER_Y2
    )

    fun startExpandAnimation(
        noteViews: List<NoteView>,
        collapseButton: TextView?,
        stackElementOffset: Int,
        elementsElevation: Int,
        parentForPost: NoteStackView
    ) {
        if (noteViews.isEmpty()) return

        val n = noteViews.size
        val totalDuration = (BASE_ANIMATION_DURATION_MS + n * ANIMATION_STEP_MS)
            .coerceAtMost(MAX_ANIMATION_DURATION_MS)
        val lastStartDelay = (n - 1) * ITEM_START_DELAY_STEP_MS
        val animDuration = (totalDuration - lastStartDelay)
            .coerceAtLeast(MIN_ITEM_ANIMATION_DURATION_MS)
            .toLong()

        noteViews.forEachIndexed { index, child ->
            child.elevation = (noteViews.size - index) + elementsElevation.toFloat()
        }
        noteViews.reversed().forEach { it.bringToFront() }

        noteViews.forEachIndexed { i, noteView ->
            val expandedTop = noteView.top
            val collapsedTop = i * stackElementOffset
            val initTransY = (collapsedTop - expandedTop).toFloat()

            noteView.translationY = initTransY

            val animator = ObjectAnimator.ofFloat(noteView, View.TRANSLATION_Y, initTransY, 0f).apply {
                duration = animDuration
                startDelay = (i * ITEM_START_DELAY_STEP_MS).toLong()
                this.interpolator = this@StackExpandAnimator.interpolator

                if (i == n - 1) {
                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            parentForPost.postDelayed({
                                animateCollapseButtonIn(collapseButton)
                            }, COLLAPSE_BUTTON_APPEAR_DELAY_MS)
                        }
                    })
                }
            }
            animator.start()
        }
    }

    private fun animateCollapseButtonIn(btn: TextView?) {
        btn?.let {
            val alphaAnim = ObjectAnimator.ofFloat(
                it,
                View.ALPHA,
                COLLAPSE_BUTTON_START_ALPHA,
                COLLAPSE_BUTTON_END_ALPHA
            )
            val scaleXAnim = ObjectAnimator.ofFloat(
                it,
                View.SCALE_X,
                COLLAPSE_BUTTON_START_SCALE,
                COLLAPSE_BUTTON_END_SCALE
            )
            val scaleYAnim = ObjectAnimator.ofFloat(
                it,
                View.SCALE_Y,
                COLLAPSE_BUTTON_START_SCALE,
                COLLAPSE_BUTTON_END_SCALE
            )

            listOf(alphaAnim, scaleXAnim, scaleYAnim).forEach { anim ->
                anim.duration = COLLAPSE_BUTTON_ANIMATION_DURATION_MS
                anim.interpolator = interpolator
                anim.start()
            }
        }
    }

    companion object {
        private const val BASE_ANIMATION_DURATION_MS = 200
        private const val ANIMATION_STEP_MS = 40
        private const val MAX_ANIMATION_DURATION_MS = 800
        private const val ITEM_START_DELAY_STEP_MS = 20
        private const val MIN_ITEM_ANIMATION_DURATION_MS = 100

        private const val COLLAPSE_BUTTON_APPEAR_DELAY_MS = 100L
        private const val COLLAPSE_BUTTON_ANIMATION_DURATION_MS = 200L
        private const val COLLAPSE_BUTTON_START_ALPHA = 0f
        private const val COLLAPSE_BUTTON_END_ALPHA = 1f
        private const val COLLAPSE_BUTTON_START_SCALE = 0.7f
        private const val COLLAPSE_BUTTON_END_SCALE = 1f

        private const val BEZIER_X1 = 0.4f
        private const val BEZIER_Y1 = 0.1f
        private const val BEZIER_X2 = 0.2f
        private const val BEZIER_Y2 = 1f
    }
}