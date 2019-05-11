package se.gustavkarlsson.skylight.android.krate

import se.gustavkarlsson.skylight.android.entities.Place

data class State(
	val isFirstRun: Boolean? = null,
	val isLocationPermissionGranted: Boolean? = null,
	val isGooglePlayServicesAvailable: Boolean? = null,
	val currentPlace: Place.Current = Place.Current(),
	val customPlaces: List<Place.Custom> = emptyList(),
	val selectedPlaceId: Int? = null,
	val throwable: Throwable? = null
) {
	val selectedPlace: Place? get() = allPlaces.find { it.id == selectedPlaceId }
	val allPlaces: List<Place> get() = listOfNotNull(currentPlace) + customPlaces
}
