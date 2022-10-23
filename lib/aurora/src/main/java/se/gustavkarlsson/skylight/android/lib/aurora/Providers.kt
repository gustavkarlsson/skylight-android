package se.gustavkarlsson.skylight.android.lib.aurora

import kotlinx.coroutines.flow.Flow
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.lib.location.Location

interface AuroraReportProvider {
    suspend fun get(location: Location): AuroraReport
    fun stream(location: Location?): Flow<LoadableAuroraReport>
}

interface AuroraForecastReportProvider {
    suspend fun get(location: Location): AuroraForecastReport
    fun stream(location: Location): Flow<Loadable<AuroraForecastReport>>
}
