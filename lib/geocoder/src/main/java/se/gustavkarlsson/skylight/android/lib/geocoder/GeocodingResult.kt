package se.gustavkarlsson.skylight.android.lib.geocoder

sealed class GeocodingResult {
    data class Success(val suggestions: List<PlaceSuggestion>) : GeocodingResult()

    sealed class Failure {
        object Io : GeocodingResult()
        object ServerError : GeocodingResult()
        object Unknown : GeocodingResult()
    }
}
