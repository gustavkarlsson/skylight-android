package se.gustavkarlsson.skylight.android.lib.location

sealed interface LocationResult {
    val location: Location? // TODO Rename to value?

    data class Success(override val location: Location) : LocationResult
    sealed interface Failure : LocationResult {
        override val location: Nothing? get() = null

        object MissingPermission : Failure
        object Unknown : Failure
    }

    // TODO Remove these?
    companion object {
        fun success(location: Location): LocationResult = Success(location)
        fun errorMissingPermission(): LocationResult = Failure.MissingPermission
        fun errorUnknown(): LocationResult = Failure.Unknown
    }
}

inline fun <T> LocationResult.map(
    onSuccess: (Location) -> T,
    onMissingPermissionError: () -> T,
    onUnknownError: () -> T
): T = when (this) {
    is LocationResult.Success -> onSuccess(location)
    LocationResult.Failure.MissingPermission -> onMissingPermissionError()
    LocationResult.Failure.Unknown -> onUnknownError()
}
