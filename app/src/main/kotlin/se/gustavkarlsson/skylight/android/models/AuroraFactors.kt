package se.gustavkarlsson.skylight.android.models

import se.gustavkarlsson.skylight.android.models.factors.Darkness
import se.gustavkarlsson.skylight.android.models.factors.GeomagActivity
import se.gustavkarlsson.skylight.android.models.factors.GeomagLocation
import se.gustavkarlsson.skylight.android.models.factors.Visibility

// TODO val or var?
data class AuroraFactors(
    val geomagActivity: GeomagActivity,
    var geomagLocation: GeomagLocation,
    var darkness: Darkness,
    var visibility: Visibility
)
