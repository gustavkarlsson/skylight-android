package se.gustavkarlsson.skylight.android.core.entities

class Chance(value: Double?) : Comparable<Chance> {
    val value: Double?

    init {
        if (value == null) {
            this.value = null
        } else {
            val coercedValue = value.coerceIn(LOWEST, HIGHEST)
            this.value = coercedValue
        }
    }

    val isKnown: Boolean
        get() = value != null

    val isPossible: Boolean
        get() = value != null && value > LOWEST

    override fun compareTo(other: Chance): Int {
        if (value != null && other.value != null) {
            return value.compareTo(other.value)
        }
        if (value == null) {
            return -1
        }
        if (other.value == null) {
            return 1
        }
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as Chance

        if (value != other.value) return false

        return true
    }

    override fun hashCode() = value?.hashCode() ?: 0

    override fun toString() = "Chance(value=$value)"

    companion object {
        private const val HIGHEST = 1.0
        private const val LOWEST = 0.0
        val UNKNOWN = Chance(null)
        val IMPOSSIBLE = Chance(LOWEST)
        val MAX = Chance(HIGHEST)
    }

    operator fun times(other: Chance): Chance {
        if (value == null || other.value == null) {
            return UNKNOWN
        }
        return Chance(value * other.value)
    }
}
