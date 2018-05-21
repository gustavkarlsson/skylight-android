package se.gustavkarlsson.skylight.android.entities

import org.threeten.bp.Instant

data class AuroraReport(
	val timestamp: Instant,
	val locationName: String?,
	val factors: AuroraFactors
)

