package se.gustavkarlsson.skylight.android.models

import android.location.Address
import org.threeten.bp.Instant

data class AuroraReport(
		val timestamp: Instant,
		val address: Address?,
		val factors: AuroraFactors
)
