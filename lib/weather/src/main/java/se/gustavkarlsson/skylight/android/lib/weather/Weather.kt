package se.gustavkarlsson.skylight.android.lib.weather

import kotlinx.datetime.Instant

data class Weather(
    val cloudPercentage: Int, // Validate/coerce
    val timestamp: Instant,
)
