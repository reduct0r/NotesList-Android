package com.example.noteslist.presentation.customviews.noteStack

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.PathInterpolator
import android.widget.TextView
import com.example.noteslist.presentation.customviews.note.NoteView

class StackExpandAnimator {

    private val interpolator = PathInterpolator(0.4f, 0.1f, 0.2f, 1f)

    fun startExpandAnimation(
        noteViews: List<NoteView>,
        collapseButton: TextView?,
        stackElementOffset: Int,
        elementsElevation: Int,
        parentForPost: NoteStackView
    ) {
        if (noteViews.isEmpty()) return

        val n = noteViews.size
        val baseDuration = 200
        val step = 40
        val maxDuration = 800
        val totalDuration = (baseDuration + n * step).coerceAtMost(maxDuration)
        val lastStartDelay = (n - 1) * 20
        val animDuration = (totalDuration - lastStartDelay).coerceAtLeast(100).toLong()

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
                startDelay = (i * 20L)
                this.interpolator = this@StackExpandAnimator.interpolator

                if (i == n - 1) {
                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            parentForPost.postDelayed({
                                animateCollapseButtonIn(collapseButton)
                            }, 100)
                        }
                    })
                }
            }
            animator.start()
        }
    }

    private fun animateCollapseButtonIn(btn: TextView?) {
        btn?.let {
            val alphaAnim = ObjectAnimator.ofFloat(it, View.ALPHA, 0f, 1f)
            val scaleXAnim = ObjectAnimator.ofFloat(it, View.SCALE_X, 0.7f, 1f)
            val scaleYAnim = ObjectAnimator.ofFloat(it, View.SCALE_Y, 0.7f, 1f)

            listOf(alphaAnim, scaleXAnim, scaleYAnim).forEach { anim ->
                anim.duration = 200L
                anim.interpolator = interpolator
                anim.start()
            }
        }
    }
}