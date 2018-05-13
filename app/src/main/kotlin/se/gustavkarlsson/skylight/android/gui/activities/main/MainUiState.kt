package se.gustavkarlsson.skylight.android.gui.activities.main

import se.gustavkarlsson.skylight.android.entities.Chance

data class MainUiState(
	val locationName: CharSequence,
	val isRefreshing: Boolean,
	val errorMessage: Int?,
	val connectivityMessage: CharSequence?,
	val chanceLevel: CharSequence,
	val timeSinceUpdate: CharSequence?,
	val darkness: Factor,
	val geomagLocation: Factor,
	val kpIndex: Factor,
	val visibility: Factor
) {
	data class Factor(val value: CharSequence, val chance: Chance)
}
