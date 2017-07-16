package se.gustavkarlsson.skylight.android.services.providers

import se.gustavkarlsson.skylight.android.entities.GeomagActivity

interface GeomagActivityProvider {
    fun getGeomagActivity(): GeomagActivity
}
