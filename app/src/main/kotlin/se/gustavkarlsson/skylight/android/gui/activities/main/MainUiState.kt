package se.gustavkarlsson.skylight.android.gui.activities.main

import se.gustavkarlsson.skylight.android.entities.AuroraReport

data class MainUiState(
	val isRefreshing: Boolean = false,
	val isConnectedToInternet: Boolean = true,
	val auroraReport: AuroraReport? = null,
	val throwable: Throwable? = null
)
