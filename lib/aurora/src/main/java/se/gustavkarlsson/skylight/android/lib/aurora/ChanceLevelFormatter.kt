package se.gustavkarlsson.skylight.android.lib.aurora

import com.ioki.textref.TextRef
import se.gustavkarlsson.skylight.android.core.R
import se.gustavkarlsson.skylight.android.core.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.core.services.Formatter

internal object ChanceLevelFormatter : Formatter<ChanceLevel> {
    override fun format(value: ChanceLevel): TextRef =
        when (value) {
            ChanceLevel.UNKNOWN -> TextRef.stringRes(R.string.aurora_chance_unknown)
            ChanceLevel.NONE -> TextRef.stringRes(R.string.aurora_chance_none)
            ChanceLevel.LOW -> TextRef.stringRes(R.string.aurora_chance_low)
            ChanceLevel.MEDIUM -> TextRef.stringRes(R.string.aurora_chance_medium)
            ChanceLevel.HIGH -> TextRef.stringRes(R.string.aurora_chance_high)
        }
}
