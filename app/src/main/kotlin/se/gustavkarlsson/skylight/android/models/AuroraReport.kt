package se.gustavkarlsson.skylight.android.models

import android.location.Address

data class AuroraReport(
		// TODO change to instant
		val timestampMillis: Long,
		val address: Address?,
		val factors: AuroraFactors
)
