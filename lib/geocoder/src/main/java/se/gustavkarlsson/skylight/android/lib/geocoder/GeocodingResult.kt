package se.gustavkarlsson.skylight.android.lib.geocoder

sealed class GeocodingResult {
    data class Success(val suggestions: List<PlaceSuggestion>) : GeocodingResult()

    sealed class Failure : GeocodingResult() {
        object Io : Failure()
        object Server : Failure()
        object Unknown : Failure()
    }
}
