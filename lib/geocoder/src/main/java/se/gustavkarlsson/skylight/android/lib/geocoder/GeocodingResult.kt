package se.gustavkarlsson.skylight.android.lib.geocoder

sealed interface GeocodingResult {
    data class Success(val suggestions: List<PlaceSuggestion>) : GeocodingResult

    sealed interface Failure : GeocodingResult {
        object Io : Failure
        object Server : Failure
        object Unknown : Failure
    }
}
