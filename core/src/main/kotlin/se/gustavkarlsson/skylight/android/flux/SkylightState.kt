package se.gustavkarlsson.skylight.android.flux

import se.gustavkarlsson.skylight.android.entities.AuroraReport

data class SkylightState(
	val locationPermission: LocationPermission = LocationPermission.UNKNOWN,
	val isRefreshing: Boolean = false,
	val justFinishedRefreshing: Boolean = false,
	val isConnectedToInternet: Boolean = true,
	val auroraReport: AuroraReport? = null,
	val newAuroraReport: AuroraReport? = null,
	val dialog: Dialog? = null,
	val throwable: Throwable? = null
) {
	data class Dialog(
		val titleResource: Int,
		val messageResource: Int
	)

	enum class LocationPermission {
		UNKNOWN, GRANTED
	}
}
