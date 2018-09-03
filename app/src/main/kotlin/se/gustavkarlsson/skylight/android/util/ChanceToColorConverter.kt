package se.gustavkarlsson.skylight.android.util

import android.content.Context
import androidx.core.app.ActivityCompat
import androidx.core.graphics.ColorUtils
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.entities.Chance

class ChanceToColorConverter(context: Context) {
    private val unknownColor = ActivityCompat.getColor(context, R.color.chance_unknown)
	private val lowestColor = ActivityCompat.getColor(context, R.color.chance_lowest)
	private val highestColor = ActivityCompat.getColor(context, R.color.chance_highest)

	fun convert(chance: Chance): Int {
        if (!chance.isKnown) {
            return unknownColor
        }

		return ColorUtils.blendARGB(lowestColor, highestColor, chance.value!!.toFloat())
    }
}
