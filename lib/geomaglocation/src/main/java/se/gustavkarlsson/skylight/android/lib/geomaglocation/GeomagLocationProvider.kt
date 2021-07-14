package se.gustavkarlsson.skylight.android.lib.geomaglocation

import se.gustavkarlsson.skylight.android.core.entities.Report
import se.gustavkarlsson.skylight.android.lib.location.Location
import se.gustavkarlsson.skylight.android.lib.location.LocationResult

interface GeomagLocationProvider {
    fun get(locationResult: LocationResult): Report<GeomagLocation>
    fun getNew(location: Location): GeomagLocation // FIXME rename
}
