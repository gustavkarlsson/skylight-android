package se.gustavkarlsson.skylight.android.flux

import se.gustavkarlsson.skylight.android.entities.AuroraReport

data class SkylightState(
	val isRefreshing: Boolean = false,
	val isConnectedToInternet: Boolean = true,
	val auroraReport: AuroraReport? = null,
	val throwable: Throwable? = null
)
