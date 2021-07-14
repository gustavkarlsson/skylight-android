package se.gustavkarlsson.skylight.android.lib.aurora

import kotlinx.coroutines.flow.Flow
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.lib.location.Location
import se.gustavkarlsson.skylight.android.lib.location.LocationResult

interface AuroraReportProvider {
    suspend fun get(getLocation: suspend () -> LocationResult): CompleteAuroraReport
    fun stream(locations: Flow<Loadable<LocationResult>>): Flow<LoadableAuroraReport>
    fun streamNew(location: Location): Flow<LoadableAuroraReport>
}
