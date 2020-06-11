package se.gustavkarlsson.skylight.android.lib.weather

import se.gustavkarlsson.skylight.android.entities.Weather
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator

interface WeatherComponent {

    fun weatherProvider(): WeatherProvider

    fun weatherEvaluator(): ChanceEvaluator<Weather>

    interface Setter {
        fun setWeatherComponent(component: WeatherComponent) {
            instance = component
        }
    }

    companion object {
        lateinit var instance: WeatherComponent
            private set
    }
}
