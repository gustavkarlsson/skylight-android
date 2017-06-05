package se.gustavkarlsson.skylight.android.models

import android.location.Address

data class AuroraReport(
		val timestampMillis: Long,
		val address: Address?,
		val factors: AuroraFactors
)
