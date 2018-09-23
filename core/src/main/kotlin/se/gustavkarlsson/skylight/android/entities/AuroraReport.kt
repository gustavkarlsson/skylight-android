package se.gustavkarlsson.skylight.android.entities

import org.threeten.bp.Instant

data class AuroraReport(
	val locationName: String?,
	val kpIndex: Report<KpIndex>,
	val geomagLocation: Report<GeomagLocation>,
	val darkness: Report<Darkness>,
	val weather: Report<Weather>
) {
	val timestamp: Instant
		get() = listOf(kpIndex, geomagLocation, darkness, weather)
			.map { it.timestamp }
			.max()!!
}

