package se.gustavkarlsson.skylight.android.services.providers

import se.gustavkarlsson.skylight.android.entities.GeomagActivity

interface GeomagActivityProvider {
    suspend fun getGeomagActivity(): GeomagActivity
}
