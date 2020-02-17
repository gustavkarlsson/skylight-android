package se.gustavkarlsson.skylight.android.entities

sealed class GeocodingResult {
    data class Success(val suggestions: List<PlaceSuggestion>) : GeocodingResult()

    sealed class Failure {
        object Io : GeocodingResult()
        object Unknown : GeocodingResult()
    }
}
