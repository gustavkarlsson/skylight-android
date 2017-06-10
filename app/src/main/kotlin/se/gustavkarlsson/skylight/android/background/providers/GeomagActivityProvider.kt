package se.gustavkarlsson.skylight.android.background.providers

import se.gustavkarlsson.skylight.android.models.factors.GeomagActivity

interface GeomagActivityProvider {
    fun getGeomagActivity(): GeomagActivity
}
