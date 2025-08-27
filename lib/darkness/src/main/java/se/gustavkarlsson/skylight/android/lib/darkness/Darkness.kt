package se.gustavkarlsson.skylight.android.lib.darkness

import kotlin.time.Instant

data class Darkness(
    val sunZenithAngle: Double,
    val timestamp: Instant,
)
