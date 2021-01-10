package se.gustavkarlsson.skylight.android.lib.darkness

import kotlinx.coroutines.flow.Flow
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.core.entities.Report
import se.gustavkarlsson.skylight.android.lib.location.LocationResult

interface DarknessProvider {
    fun get(location: LocationResult): Report<Darkness>
    fun stream(locations: Flow<Loadable<LocationResult>>): Flow<Loadable<Report<Darkness>>>
}
