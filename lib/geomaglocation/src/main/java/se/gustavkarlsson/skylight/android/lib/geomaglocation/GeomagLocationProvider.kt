package se.gustavkarlsson.skylight.android.lib.geomaglocation

import kotlinx.coroutines.flow.Flow
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.core.entities.Report
import se.gustavkarlsson.skylight.android.lib.location.LocationResult

interface GeomagLocationProvider {
    fun get(location: LocationResult): Report<GeomagLocation>
    fun stream(locations: Flow<Loadable<LocationResult>>): Flow<Loadable<Report<GeomagLocation>>>
}
