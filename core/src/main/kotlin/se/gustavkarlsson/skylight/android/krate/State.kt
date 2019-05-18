package se.gustavkarlsson.skylight.android.krate

import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.entities.Place

data class State(
	val isFirstRun: Boolean? = null,
	val isLocationPermissionGranted: Boolean? = null,
	val isGooglePlayServicesAvailable: Boolean? = null,
	val places: List<Place> = listOf(Place.Current),
	val selectedPlace: Place? = null,
	val auroraReports: Map<Place, AuroraReport> = emptyMap(),
	val throwable: Throwable? = null
) {
	val selectedAuroraReport: AuroraReport? get() = auroraReports[selectedPlace]
}
