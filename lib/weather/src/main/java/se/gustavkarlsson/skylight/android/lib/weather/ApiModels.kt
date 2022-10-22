package se.gustavkarlsson.skylight.android.lib.weather

import kotlinx.serialization.Serializable

@Serializable
internal data class OpenWeatherMapWeather(
    val clouds: Clouds,
    val dt: Long, // epoch seconds
) {
    @Serializable
    data class Clouds(
        /**
         * Cloud percentage
         */
        val all: Int,
    )
}

@Serializable
internal data class OpenWeatherMapWeatherForecast(val list: List<OpenWeatherMapWeather>)
