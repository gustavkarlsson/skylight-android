package se.gustavkarlsson.skylight.android.core.entities

enum class ChanceLevel {
    // Warning. The names are used to report analytics data
    UNKNOWN,
    NONE,
    LOW,
    MEDIUM,
    HIGH;

    infix fun isGreaterOrEqual(triggerLevel: TriggerLevel): Boolean =
        when (this) {
            UNKNOWN, NONE -> false
            LOW -> triggerLevel == TriggerLevel.LOW
            MEDIUM -> triggerLevel in (listOf(TriggerLevel.LOW, TriggerLevel.MEDIUM))
            HIGH -> triggerLevel in (listOf(TriggerLevel.LOW, TriggerLevel.MEDIUM, TriggerLevel.HIGH))
        }

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
