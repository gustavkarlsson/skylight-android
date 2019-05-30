package se.gustavkarlsson.skylight.android.lib.geocoder

import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.PlaceSuggestion

interface Geocoder {
	fun geocode(locationName: String): Single<List<PlaceSuggestion>>
}
