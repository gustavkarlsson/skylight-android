package se.gustavkarlsson.skylight.android.services.evaluation

import se.gustavkarlsson.skylight.android.R

enum class ChanceLevel(val resourceId: Int) {
    // Warning. These ordinals relate directly to pref_trigger_level_values
    UNKNOWN(R.string.aurora_chance_unknown),
    NONE(R.string.aurora_chance_none),
    LOW(R.string.aurora_chance_low),
    MEDIUM(R.string.aurora_chance_medium),
    HIGH(R.string.aurora_chance_high);

    companion object {
        fun fromChance(chance: Chance): ChanceLevel {
			return when (chance) {
				Chance.UNKNOWN -> UNKNOWN
				Chance.IMPOSSIBLE -> NONE
				else -> when (chance.value!!) {
					in 0.0..0.33  -> LOW
					in 0.33..0.66 -> MEDIUM
					else          -> HIGH
				}
			}
        }
    }
}
