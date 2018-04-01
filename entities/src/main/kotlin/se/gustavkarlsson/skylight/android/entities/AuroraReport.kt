package se.gustavkarlsson.skylight.android.entities

import org.threeten.bp.Instant

data class AuroraReport(
	val timestamp: Instant,
	val locationName: String?,
	val factors: AuroraFactors
) {
	companion object {
		val empty = AuroraReport(
			Instant.EPOCH,
			null,
			AuroraFactors(KpIndex(), GeomagLocation(), Darkness(), Visibility())
		)
	}
}

