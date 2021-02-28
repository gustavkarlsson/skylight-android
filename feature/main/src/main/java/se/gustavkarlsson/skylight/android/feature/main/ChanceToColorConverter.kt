package se.gustavkarlsson.skylight.android.feature.main

import android.content.Context
import android.graphics.Color
import androidx.core.graphics.ColorUtils
import se.gustavkarlsson.skylight.android.lib.ui.legacy.extensions.toArgb

internal class ChanceToColorConverter(context: Context) {
    private val lowestColor = R.color.chance_lowest.toArgb(context)
    private val middleColor = R.color.chance_middle.toArgb(context)
    private val highestColor = R.color.chance_highest.toArgb(context)

    fun convert(chance: Double?): Int {
        if (chance == null) {
            return Color.TRANSPARENT
        }

        return if (chance < MIDDLE_COLOR_CHANCE) {
            val blendAmount = (chance / MIDDLE_COLOR_CHANCE).toFloat()
            ColorUtils.blendARGB(lowestColor, middleColor, blendAmount)
        } else {
            val blendAmount = ((chance - MIDDLE_COLOR_CHANCE) / MIDDLE_COLOR_CHANCE).toFloat()
            ColorUtils.blendARGB(middleColor, highestColor, blendAmount)
        }
    }

    companion object {
        private const val MIDDLE_COLOR_CHANCE = 0.5
        const val UNKNOWN_COLOR = Color.TRANSPARENT
    }
}
