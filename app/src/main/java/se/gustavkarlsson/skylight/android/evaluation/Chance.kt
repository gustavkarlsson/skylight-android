package se.gustavkarlsson.skylight.android.evaluation

class Chance private constructor(value: Double?) : Comparable<Chance> {
    val value: Double?

    init {
        if (value == null) {
            this.value = null
        } else {
			val limitedValue = Math.max(0.0, Math.min(1.0, value))
			this.value = limitedValue
		}
    }

    val isKnown: Boolean
        get() = value != null

    val isPossible: Boolean
        get() = value != null && value > 0.0

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

	override fun hashCode(): Int {
		return value?.hashCode() ?: 0
	}

	override fun toString(): String {
		return "Chance(value=$value)"
	}

	companion object {

        // TODO convert these to singletons
        fun unknown(): Chance {
            return Chance(null)
        }

        fun impossible(): Chance {
            return Chance(0.0)
        }

        fun of(value: Double): Chance {
            return Chance(value)
        }
    }
}
