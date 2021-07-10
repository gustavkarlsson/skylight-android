package se.gustavkarlsson.skylight.android.lib.location

sealed class LocationResult {
    abstract val location: Location?

    // TODO Move to extension?
    inline fun <T> map(
        onSuccess: (Location) -> T,
        onMissingPermissionError: () -> T,
        onUnknownError: () -> T
    ): T = when (this) {
        is Success -> onSuccess(location)
        Failure.MissingPermission -> onMissingPermissionError()
        Failure.Unknown -> onUnknownError()
    }

    data class Success(override val location: Location) : LocationResult()
    sealed class Failure : LocationResult() {
        override val location: Nothing? = null

        object MissingPermission : Failure()
        object Unknown : Failure()
    }

    companion object {
        fun success(location: Location): LocationResult = Success(location)
        fun errorMissingPermission(): LocationResult = Failure.MissingPermission
        fun errorUnknown(): LocationResult = Failure.Unknown
    }
}
