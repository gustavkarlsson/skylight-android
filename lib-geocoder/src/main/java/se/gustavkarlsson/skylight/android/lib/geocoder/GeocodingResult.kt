package se.gustavkarlsson.skylight.android.lib.geocoder

import se.gustavkarlsson.skylight.android.entities.PlaceSuggestion

sealed class GeocodingResult {
    data class Success(val suggestions: List<PlaceSuggestion>) : GeocodingResult()

    sealed class Failure {
        object Io : GeocodingResult()
        object Unknown : GeocodingResult()
    }
}
