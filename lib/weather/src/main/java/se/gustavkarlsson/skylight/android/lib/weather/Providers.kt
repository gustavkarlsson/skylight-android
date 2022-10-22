package se.gustavkarlsson.skylight.android.lib.weather

import kotlinx.coroutines.flow.Flow
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.lib.location.Location

interface WeatherProvider {
    suspend fun get(location: Location, fresh: Boolean = false): WeatherResult
    fun stream(location: Location): Flow<Loadable<WeatherResult>>
}

interface WeatherForecastProvider {
    suspend fun get(location: Location, fresh: Boolean = false): WeatherForecastResult
    fun stream(location: Location): Flow<Loadable<WeatherForecastResult>>
}
