package se.gustavkarlsson.skylight.android.lib.weather

import java.util.function.IntFunction
import kotlin.time.Instant

data class Weather(
    val cloudPercentage: Int, // Validate/coerce
    val timestamp: Instant,
)

data class WeatherForecast(private val weathers: List<Weather>) : List<Weather> by weathers {
    @Suppress("OVERRIDE_DEPRECATION")
    override fun <T : Any?> toArray(generator: IntFunction<Array<out T>>): Array<out T> {
        @Suppress("DEPRECATION")
        return super.toArray(generator)
    }
}
