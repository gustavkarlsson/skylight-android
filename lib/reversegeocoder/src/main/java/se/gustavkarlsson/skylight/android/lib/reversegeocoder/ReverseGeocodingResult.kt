package se.gustavkarlsson.skylight.android.lib.reversegeocoder

import java.io.IOException

sealed class ReverseGeocodingResult {
    data class Success(val name: String) : ReverseGeocodingResult()
    sealed class Failure : ReverseGeocodingResult() {
        object NotFound : Failure()
        object Location : Failure()
        object LocationPermission : Failure()
        data class Io(val exception: IOException) : Failure()
    }
}
