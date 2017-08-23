package se.gustavkarlsson.skylight.android.services.providers

import se.gustavkarlsson.skylight.android.entities.GeomagLocation

interface GeomagLocationProvider {
    fun getGeomagLocation(latitude: Double, longitude: Double): GeomagLocation
}