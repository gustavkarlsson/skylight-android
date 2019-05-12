package se.gustavkarlsson.skylight.android.feature.addplace

import android.location.Address
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.entities.PlaceSuggestion
import se.gustavkarlsson.skylight.android.services.Geocoder
import timber.log.Timber
import android.location.Geocoder as AndroidSystemGeocoder

internal class AndroidGeocoder(private val geocoder: AndroidSystemGeocoder) : Geocoder {

	override fun geocode(locationName: String): Single<List<PlaceSuggestion>> =
		Single
			.fromCallable { geocodeBlocking(locationName) }
			.subscribeOn(Schedulers.io())
			.onErrorReturnItem(emptyList())

	private fun geocodeBlocking(locationName: String): List<PlaceSuggestion> =
		geocoder.getFromLocationName(locationName, 5)
			.map(::createSuggestion)

	private fun createSuggestion(address: Address): PlaceSuggestion {
		val lines = address.safeLines
		val fullName = lines.take(3).joinToString(", ")
		val simpleName = lines.first()
		return PlaceSuggestion(Location(address.latitude, address.longitude), fullName, simpleName)
	}
}

private val Address.safeLines: List<String>
	get() = listOfNotNull(
		thoroughfare,
		locality,
		featureName,
		adminArea,
		countryName
	).distinct().filter { !it.matches("[0-9]+".toRegex()) }
