package se.gustavkarlsson.skylight.android.lib.darkness

import org.threeten.bp.Instant

data class Darkness(
    val sunZenithAngle: Double,
    val timestamp: Instant,
)
