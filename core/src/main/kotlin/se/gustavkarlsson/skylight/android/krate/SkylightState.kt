package se.gustavkarlsson.skylight.android.krate

import se.gustavkarlsson.skylight.android.entities.CurrentPlace
import se.gustavkarlsson.skylight.android.entities.CustomPlace
import se.gustavkarlsson.skylight.android.entities.Place

data class SkylightState(
	val isFirstRun: Boolean? = null,
	val isLocationPermissionGranted: Boolean? = null,
	val isGooglePlayServicesAvailable: Boolean? = null,
	val currentPlace: CurrentPlace = CurrentPlace(),
	val customPlaces: List<CustomPlace> = emptyList(),
	val selectedPlace: Place = currentPlace,
	val throwable: Throwable? = null
) {
	val allPlaces: List<Place> get() = listOfNotNull(currentPlace) + customPlaces
}
