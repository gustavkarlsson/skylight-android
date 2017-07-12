package se.gustavkarlsson.skylight.android.background.providers

import se.gustavkarlsson.skylight.android.entities.GeomagActivity

interface GeomagActivityProvider {
    fun getGeomagActivity(): GeomagActivity
}
