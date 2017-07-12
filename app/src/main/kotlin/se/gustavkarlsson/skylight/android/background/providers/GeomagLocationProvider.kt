package se.gustavkarlsson.skylight.android.background.providers

import se.gustavkarlsson.skylight.android.entities.GeomagLocation

interface GeomagLocationProvider {
    fun getGeomagLocation(latitude: Double, longitude: Double): GeomagLocation
}
