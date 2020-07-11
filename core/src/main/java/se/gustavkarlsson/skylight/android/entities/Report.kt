package se.gustavkarlsson.skylight.android.entities

import org.threeten.bp.Instant

sealed class Report<out T : Any> {
    abstract val timestamp: Instant

    data class Success<T : Any>(
        val value: T,
        override val timestamp: Instant
    ) : Report<T>()

    data class Error(
        val cause: Cause,
        override val timestamp: Instant
    ) : Report<Nothing>()

    companion object {
        fun <T: Any> success(value: T, timestamp: Instant): Report<T> = Success(value, timestamp)
        fun <T: Any> error(cause: Cause, timestamp: Instant): Report<T> = Error(cause, timestamp)
    }
}
