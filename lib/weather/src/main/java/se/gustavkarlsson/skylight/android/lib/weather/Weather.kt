package se.gustavkarlsson.skylight.android.lib.weather

import org.threeten.bp.Instant

data class Weather(
    val cloudPercentage: Int, // Validate/coerce
    val timestamp: Instant,
)
