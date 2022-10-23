package se.gustavkarlsson.skylight.android.lib.darkness

import kotlinx.datetime.Instant

data class Darkness(
    val sunZenithAngle: Double,
    val timestamp: Instant,
)

data class DarknessForecast(private val darknesses: List<Darkness>) : List<Darkness> by darknesses