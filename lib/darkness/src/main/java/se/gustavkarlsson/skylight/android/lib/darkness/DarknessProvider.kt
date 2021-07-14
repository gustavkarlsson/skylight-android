package se.gustavkarlsson.skylight.android.lib.darkness

import kotlinx.coroutines.flow.Flow
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.core.entities.Report
import se.gustavkarlsson.skylight.android.lib.location.Location
import se.gustavkarlsson.skylight.android.lib.location.LocationResult

interface DarknessProvider {
    fun get(locationResult: LocationResult): Report<Darkness>
    fun stream(location: Location): Flow<Loadable<Report<Darkness>>>
}
