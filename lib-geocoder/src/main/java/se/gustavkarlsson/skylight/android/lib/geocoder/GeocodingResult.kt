package se.gustavkarlsson.skylight.android.lib.geocoder

import se.gustavkarlsson.skylight.android.entities.Location

sealed class GeocodingResult {
	data class Success(val suggestions: List<PlaceSuggestion>) : GeocodingResult()

	sealed class Failure {
		object Io : GeocodingResult()
		object Other : GeocodingResult()
	}
}

data class PlaceSuggestion(
	val location: Location,
	val fullName: String,
	val simpleName: String
)
