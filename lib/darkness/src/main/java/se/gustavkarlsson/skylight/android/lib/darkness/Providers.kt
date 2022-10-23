package se.gustavkarlsson.skylight.android.lib.darkness

import kotlinx.coroutines.flow.Flow
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.lib.location.Location

interface DarknessProvider {
    fun get(location: Location): Darkness
    fun stream(location: Location): Flow<Loadable<Darkness>> // TODO don't wrap in loadable
}

interface DarknessForecastProvider {
    fun get(location: Location): DarknessForecast
    fun stream(location: Location): Flow<Loadable<DarknessForecast>> // TODO don't wrap in loadable
}
