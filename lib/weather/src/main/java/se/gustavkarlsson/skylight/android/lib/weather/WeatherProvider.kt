package se.gustavkarlsson.skylight.android.lib.weather

import kotlinx.coroutines.flow.Flow
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.core.entities.Report
import se.gustavkarlsson.skylight.android.lib.location.Location
import se.gustavkarlsson.skylight.android.lib.location.LocationResult

interface WeatherProvider {
    suspend fun get(locationResult: LocationResult, fresh: Boolean = false): Report<Weather>
    fun stream(location: Location): Flow<Loadable<Report<Weather>>>
}
