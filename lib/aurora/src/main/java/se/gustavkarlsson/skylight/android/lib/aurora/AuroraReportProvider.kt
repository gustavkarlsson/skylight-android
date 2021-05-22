package se.gustavkarlsson.skylight.android.lib.aurora

import kotlinx.coroutines.flow.Flow
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.lib.location.LocationResult

interface AuroraReportProvider {
    suspend fun get(getLocation: suspend () -> LocationResult): CompleteAuroraReport // TODO Why suspending lambda?
    fun stream(locations: Flow<Loadable<LocationResult>>): Flow<LoadableAuroraReport>
}
