package se.gustavkarlsson.skylight.android.krate

import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.entities.Place

data class SkylightState(
	val settings: Settings,
	val isFirstRun: Boolean? = null,
	val isLocationPermissionGranted: Boolean? = null,
	val isGooglePlayServicesAvailable: Boolean? = null,
	val currentLocationAuroraReport: AuroraReport? = null,
	val selectedPlace: Place? = null,
	val customPlaces: List<Place> = emptyList(),
	val throwable: Throwable? = null
) {

	data class Settings(
		val notificationsEnabled: Boolean,
		val triggerLevel: ChanceLevel
	)
}
