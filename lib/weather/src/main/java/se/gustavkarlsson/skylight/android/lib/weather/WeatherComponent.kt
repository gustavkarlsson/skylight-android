package se.gustavkarlsson.skylight.android.lib.weather

import com.squareup.anvil.annotations.ContributesTo
import se.gustavkarlsson.skylight.android.core.AppScopeMarker
import se.gustavkarlsson.skylight.android.core.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.core.services.Formatter

@ContributesTo(AppScopeMarker::class)
interface WeatherComponent {

    fun weatherProvider(): WeatherProvider

    fun weatherChanceEvaluator(): ChanceEvaluator<Weather>

    fun weatherFormatter(): Formatter<Weather>

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
