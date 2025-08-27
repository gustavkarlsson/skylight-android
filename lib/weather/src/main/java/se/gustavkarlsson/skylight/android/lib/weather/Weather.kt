package se.gustavkarlsson.skylight.android.lib.weather

import kotlin.time.Instant

data class Weather(
    val cloudPercentage: Int, // Validate/coerce
    val timestamp: Instant,
)
