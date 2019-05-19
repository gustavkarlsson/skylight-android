package se.gustavkarlsson.skylight.android.entities

import org.threeten.bp.Instant

class Report<T : Any> private constructor(
	val value: T?,
	val error: Int?,
	val timestamp: Instant
) {
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false

		other as Report<*>

		if (value != other.value) return false
		if (error != other.error) return false
		if (timestamp != other.timestamp) return false

		return true
	}

	override fun hashCode(): Int {
		var result = value?.hashCode() ?: 0
		result = 31 * result + (error ?: 0)
		result = 31 * result + timestamp.hashCode()
		return result
	}

	override fun toString(): String {
		return if (value != null) {
			"Report(value=$value, timestamp=$timestamp)"
		} else {
			"Report(error=$error, timestamp=$timestamp)"
		}
	}

	companion object {
		fun <T : Any> success(value: T, timestamp: Instant): Report<T> {
			return Report(value, null, timestamp)
		}

		fun <T : Any> error(error: Int, timestamp: Instant): Report<T> {
			return Report(null, error, timestamp)
		}
	}
}
