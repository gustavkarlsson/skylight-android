package se.gustavkarlsson.skylight.android.feature.addplace

import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.entities.PlaceSuggestion
import se.gustavkarlsson.skylight.android.services.Geocoder
import android.location.Geocoder as AndroidSystemGeocoder

internal class AndroidGeocoder(private val geocoder: AndroidSystemGeocoder) : Geocoder {
	override fun geocode(locationName: String): Single<List<PlaceSuggestion>> {
		return Single.fromCallable {
			geocodeBlocking(locationName)
		}.subscribeOn(Schedulers.io())
	}

	private fun geocodeBlocking(locationName: String): List<PlaceSuggestion> =
		geocoder.getFromLocationName(locationName, 10)
			.map {
				val lines = (0..it.maxAddressLineIndex).map(it::getAddressLine)
				PlaceSuggestion(Location(it.latitude, it.longitude), lines)
			}
}
