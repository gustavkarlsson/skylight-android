package se.gustavkarlsson.skylight.android.services_impl.formatters

import android.content.Context
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.services.formatters.SingleValueFormatter

class ChanceLevelFormatter(private val context: Context) : SingleValueFormatter<ChanceLevel> {
	override fun format(value: ChanceLevel): CharSequence {
		return when (value) {
			ChanceLevel.UNKNOWN -> context.getString(R.string.aurora_chance_unknown)
			ChanceLevel.NONE -> context.getString(R.string.aurora_chance_none)
			ChanceLevel.LOW -> context.getString(R.string.aurora_chance_low)
			ChanceLevel.MEDIUM -> context.getString(R.string.aurora_chance_medium)
			ChanceLevel.HIGH -> context.getString(R.string.aurora_chance_high)
		}
	}
}
