package se.gustavkarlsson.skylight.android.lib.ui.extensions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import androidx.core.view.isVisible

const val FADE_DURATION_MILLIS = 200

fun View.fadeToVisible(visible: Boolean, invisibility: Int = View.GONE) {
    if (visible) fadeIn()
    else fadeOut(invisibility)
}

fun View.fadeIn() {
    if (isVisible && alpha == 1f) {
        return
    }
    if (!isVisible) {
        alpha = 0f
        visibility = View.VISIBLE
    }
    val duration = ((1f - alpha) * FADE_DURATION_MILLIS).toLong()
    animate()
        .alpha(1f)
        .setDuration(duration)
        .setListener(null)
}

fun View.fadeOut(invisibility: Int = View.GONE) {
    if (!isVisible) {
        return
    }
    val duration = (alpha * FADE_DURATION_MILLIS).toLong()
    animate()
        .alpha(0f)
        .setDuration(duration)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                visibility = invisibility
            }
        })
}
