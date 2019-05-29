package se.gustavkarlsson.skylight.android.services_impl.formatters

import com.ioki.textref.TextRef
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.services.Formatter

object ChanceLevelFormatter : Formatter<ChanceLevel> {
	override fun format(value: ChanceLevel): TextRef {
		return when (value) {
			ChanceLevel.UNKNOWN -> TextRef(R.string.aurora_chance_unknown)
			ChanceLevel.NONE -> TextRef(R.string.aurora_chance_none)
			ChanceLevel.LOW -> TextRef(R.string.aurora_chance_low)
			ChanceLevel.MEDIUM -> TextRef(R.string.aurora_chance_medium)
			ChanceLevel.HIGH -> TextRef(R.string.aurora_chance_high)
		}
	}
}
