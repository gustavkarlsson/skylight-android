package se.gustavkarlsson.skylight.android.entities

enum class ChanceLevel {
	// Warning. These ordinals relate directly to pref_trigger_level_values
	UNKNOWN,
	NONE,
	LOW,
	MEDIUM,
	HIGH;

	companion object {
		fun fromChance(chance: Chance): ChanceLevel {
			return when (chance) {
				Chance.UNKNOWN -> UNKNOWN
				Chance.IMPOSSIBLE -> NONE
				else -> when (chance.value!!) {
					in 0.0..0.33 -> LOW
					in 0.33..0.66 -> MEDIUM
					else -> HIGH
				}
			}
		}
	}
}
