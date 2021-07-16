package se.gustavkarlsson.skylight.android.lib.geomaglocation

import se.gustavkarlsson.skylight.android.lib.location.Location

interface GeomagLocationProvider {
    fun get(location: Location): GeomagLocation
}
