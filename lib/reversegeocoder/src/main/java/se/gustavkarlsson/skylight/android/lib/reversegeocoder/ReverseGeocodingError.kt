package se.gustavkarlsson.skylight.android.lib.reversegeocoder

import java.io.IOException

sealed interface ReverseGeocodingError {
    object NotFound : ReverseGeocodingError
    object NoLocation : ReverseGeocodingError
    object NoLocationPermission : ReverseGeocodingError
    data class Io(val exception: IOException) : ReverseGeocodingError
    data class Unknown(val throwable: Throwable) : ReverseGeocodingError
}
