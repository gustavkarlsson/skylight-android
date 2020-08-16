package se.gustavkarlsson.skylight.android.lib.weather

import kotlinx.coroutines.flow.Flow
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.core.entities.Report
import se.gustavkarlsson.skylight.android.lib.location.LocationResult

interface WeatherProvider {
    suspend fun get(location: LocationResult, fresh: Boolean = false): Report<Weather>
    fun stream(locations: Flow<Loadable<LocationResult>>): Flow<Loadable<Report<Weather>>>
}
