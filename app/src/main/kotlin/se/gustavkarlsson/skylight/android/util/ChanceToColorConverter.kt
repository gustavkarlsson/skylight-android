package se.gustavkarlsson.skylight.android.util

import android.content.Context
import android.graphics.Color
import androidx.core.graphics.ColorUtils
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.entities.Chance
import se.gustavkarlsson.skylight.android.extensions.toArgb

class ChanceToColorConverter(context: Context) {
	private val lowestColor = R.color.chance_lowest.toArgb(context)
	private val middleColor = R.color.chance_middle.toArgb(context)
	private val highestColor = R.color.chance_highest.toArgb(context)

	fun convert(chance: Chance): Int {
        if (!chance.isKnown) {
            return Color.TRANSPARENT
        }

		val firstColor = if (chance.value!! < 0.5) lowestColor else middleColor
		val lastColor = if (chance.value!! < 0.5) middleColor else highestColor
		return ColorUtils.blendARGB(firstColor, lastColor, chance.value!!.toFloat())
    }
}
