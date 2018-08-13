package se.gustavkarlsson.skylight.android.krate

import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.entities.ChanceLevel

data class SkylightState(
	val settings: Settings,
	val locationPermission: LocationPermission = LocationPermission.UNKNOWN,
	val isRefreshing: Boolean = false,
	val justFinishedRefreshing: Boolean = false,
	val isConnectedToInternet: Boolean = true,
	val auroraReport: AuroraReport? = null,
	val newAuroraReport: AuroraReport? = null,
	val throwable: Throwable? = null
) {
	enum class LocationPermission {
		UNKNOWN, GRANTED
	}

	data class Settings(
		val notificationsEnabled: Boolean,
		val triggerLevel: ChanceLevel
	)
}
