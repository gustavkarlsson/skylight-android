package se.gustavkarlsson.skylight.android.evaluation

import se.gustavkarlsson.skylight.android.R

enum class ChanceLevel(val resourceId: Int) {
    // Warning. These ordinals relate directly to pref_trigger_level_values
    UNKNOWN(R.string.aurora_chance_unknown),
    NONE(R.string.aurora_chance_none),
    LOW(R.string.aurora_chance_low),
    MEDIUM(R.string.aurora_chance_medium),
    HIGH(R.string.aurora_chance_high);

	// TODO get rid of companion
    companion object {
        fun fromChance(chance: Chance): ChanceLevel {
            if (!chance.isKnown) {
                return UNKNOWN
            }
            if (!chance.isPossible) {
                return NONE
            }

            val value = chance.value!!
            if (value < 0.33) {
                return LOW
            } else if (value < 0.66) {
                return MEDIUM
            } else {
                return HIGH
            }
        }
    }
}
