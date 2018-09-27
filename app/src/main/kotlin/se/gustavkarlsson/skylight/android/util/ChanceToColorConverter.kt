package se.gustavkarlsson.skylight.android.util

import android.content.Context
import android.graphics.Color
import androidx.core.graphics.ColorUtils
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.entities.Chance
import se.gustavkarlsson.skylight.android.extensions.toArgb

class ChanceToColorConverter(context: Context) {
	private val lowestColor = R.color.chance_lowest.toArgb(context)
	private val highestColor = R.color.chance_highest.toArgb(context)

	fun convert(chance: Chance): Int {
        if (!chance.isKnown) {
            return Color.TRANSPARENT
        }

		return ColorUtils.blendARGB(lowestColor, highestColor, chance.value!!.toFloat())
    }
}
