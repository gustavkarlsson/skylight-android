package se.gustavkarlsson.skylight.android.lib.weather

import kotlinx.serialization.Serializable

@Serializable
internal data class OpenWeatherMapWeather(val clouds: Clouds) {
    @Serializable
    data class Clouds(
        /**
         * Cloud percentage
         */
        val all: Int,
    )
}
