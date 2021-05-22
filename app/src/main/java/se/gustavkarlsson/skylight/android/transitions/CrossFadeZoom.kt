package se.gustavkarlsson.skylight.android.transitions

import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ScaleFactor
import androidx.compose.ui.layout.lerp
import com.zachklipp.compose.backstack.BackstackTransition

object CrossFadeZoom : BackstackTransition {
    override fun Modifier.modifierForScreen(
        visibility: State<Float>,
        isTop: Boolean,
    ): Modifier {
        val alpha = visibility.value
        val scale by if (isTop) {
            derivedStateOf { lerp(ScaleFactor(0.9f), ScaleFactor(1f), visibility.value) }
        } else {
            derivedStateOf { lerp(ScaleFactor(1.1f), ScaleFactor(1f), visibility.value) }
        }
        return alpha(alpha).scale(scale)
    }
}

private fun ScaleFactor(scale: Float) = ScaleFactor(scale, scale)

private fun Modifier.scale(scale: ScaleFactor) = scale(scale.scaleX, scale.scaleY)
