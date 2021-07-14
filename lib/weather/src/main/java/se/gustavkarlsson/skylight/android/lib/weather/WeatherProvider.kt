package se.gustavkarlsson.skylight.android.lib.weather

import kotlinx.coroutines.flow.Flow
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.core.entities.Report
import se.gustavkarlsson.skylight.android.lib.location.Location

interface WeatherProvider {
    suspend fun get(location: Location, fresh: Boolean = false): Report<Weather>
    fun stream(location: Location): Flow<Loadable<Report<Weather>>>
}
