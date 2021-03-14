package se.gustavkarlsson.skylight.android.lib.ui.compose

import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@Composable
fun MultiColorLinearProgressIndicator(
    modifier: Modifier,
    progress: Float?,
) {
    val color = getColor(progress)
    val backgroundColor = if (progress != null) {
        color.copy(alpha = ProgressIndicatorDefaults.IndicatorBackgroundOpacity)
    } else Colors.onSurface.copy(alpha = ProgressIndicatorDefaults.IndicatorBackgroundOpacity)
    LinearProgressIndicator(
        modifier = modifier,
        color = color,
        backgroundColor = backgroundColor,
        progress = progress ?: 0f,
    )
}

private const val MIDDLE_COLOR_CHANCE = 0.5

@Composable
private fun getColor(progress: Float?): Color {
    if (progress == null) {
        return Color.Transparent
    }

    return if (progress < MIDDLE_COLOR_CHANCE) {
        val blendAmount = (progress / MIDDLE_COLOR_CHANCE).toFloat()
        blend(Colors.progressLowest, Colors.progressMedium, blendAmount)
    } else {
        val blendAmount = ((progress - MIDDLE_COLOR_CHANCE) / MIDDLE_COLOR_CHANCE).toFloat()
        blend(Colors.progressMedium, Colors.progressHighest, blendAmount)
    }
}

private fun blend(first: Color, second: Color, ratio: Float): Color {
    val inverseRatio = 1 - ratio
    val alpha = (first.alpha * inverseRatio) + (second.alpha * ratio)
    val red = (first.red * inverseRatio) + (second.red * ratio)
    val green = (first.green * inverseRatio) + (second.green * ratio)
    val blue = (first.blue * inverseRatio) + (second.blue * ratio)
    return Color(red, green, blue, alpha)
}
