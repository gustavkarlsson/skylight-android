package se.gustavkarlsson.skylight.android.lib.darkness

import kotlinx.datetime.Instant

data class Darkness(
    val sunZenithAngle: Double,
    val timestamp: Instant,
)
