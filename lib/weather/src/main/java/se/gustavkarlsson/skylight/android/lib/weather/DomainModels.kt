package se.gustavkarlsson.skylight.android.lib.weather

import kotlin.time.Instant

data class Weather(
    val cloudPercentage: Int, // Validate/coerce
    val timestamp: Instant,
)

data class WeatherForecast(private val weathers: List<Weather>) : List<Weather> by weathers
