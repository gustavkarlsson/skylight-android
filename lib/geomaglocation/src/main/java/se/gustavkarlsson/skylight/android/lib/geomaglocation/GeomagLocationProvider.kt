package se.gustavkarlsson.skylight.android.lib.geomaglocation

import se.gustavkarlsson.skylight.android.core.entities.Report
import se.gustavkarlsson.skylight.android.lib.location.Location

interface GeomagLocationProvider {
    fun get(location: Location): Report<GeomagLocation>
    fun getNew(location: Location): GeomagLocation // FIXME rename
}
