package se.gustavkarlsson.skylight.android.background.providers

import se.gustavkarlsson.skylight.android.models.GeomagActivity

interface GeomagActivityProvider {
    fun getGeomagActivity(): GeomagActivity
}
