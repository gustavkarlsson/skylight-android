package se.gustavkarlsson.skylight.android.lib.darkness

import kotlinx.coroutines.flow.Flow
import se.gustavkarlsson.skylight.android.lib.location.Location

interface DarknessProvider {
    fun get(location: Location): Darkness
    fun stream(location: Location): Flow<Darkness>
}

interface DarknessForecastProvider {
    fun get(location: Location): DarknessForecast
    fun stream(location: Location): Flow<DarknessForecast>
}
