package se.gustavkarlsson.skylight.android.lib.weather

import org.threeten.bp.Instant

data class Weather(
    val cloudPercentage: Int,
    val timestamp: Instant,
)
