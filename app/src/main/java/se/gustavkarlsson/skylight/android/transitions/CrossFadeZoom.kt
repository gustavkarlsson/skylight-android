package se.gustavkarlsson.skylight.android.transitions

import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.util.lerp
import com.zachklipp.compose.backstack.BackstackTransition

object CrossFadeZoom : BackstackTransition {
    override fun Modifier.modifierForScreen(
        visibility: State<Float>,
        isTop: Boolean,
    ): Modifier {
        val alpha = visibility.value
        val scale by if (isTop) {
            derivedStateOf { lerp(0.9f, 1f, visibility.value) }
        } else {
            derivedStateOf { lerp(1.1f, 1f, visibility.value) }
        }
        return alpha(alpha).scale(scale)
    }
}
