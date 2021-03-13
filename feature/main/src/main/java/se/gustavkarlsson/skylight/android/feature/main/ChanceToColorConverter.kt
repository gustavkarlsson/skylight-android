package se.gustavkarlsson.skylight.android.feature.main

import androidx.compose.ui.graphics.Color

internal object ChanceToColorConverter {
    // FIXME Move to theme colors
    private val lowestColor = Color(0xFFF44336)
    private val middleColor = Color(0xFFF0F436)
    private val highestColor = Color(0xFF34B4E2)

    fun convert(chance: Double?): Color {
        if (chance == null) {
            return Color.Transparent
        }

        return if (chance < MIDDLE_COLOR_CHANCE) {
            val blendAmount = (chance / MIDDLE_COLOR_CHANCE).toFloat()
            blend(lowestColor, middleColor, blendAmount)
        } else {
            val blendAmount = ((chance - MIDDLE_COLOR_CHANCE) / MIDDLE_COLOR_CHANCE).toFloat()
            blend(middleColor, highestColor, blendAmount)
        }
    }

    private const val MIDDLE_COLOR_CHANCE = 0.5
    val UNKNOWN_COLOR = Color.Transparent
}

private fun blend(first: Color, second: Color, ratio: Float): Color {
    val inverseRatio = 1 - ratio
    val alpha = (first.alpha * inverseRatio) + (second.alpha * ratio)
    val red = (first.red * inverseRatio) + (second.red * ratio)
    val green = (first.green * inverseRatio) + (second.green * ratio)
    val blue = (first.blue * inverseRatio) + (second.blue * ratio)
    return Color(red, green, blue, alpha)
}
