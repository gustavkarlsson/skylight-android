package se.gustavkarlsson.skylight.android.lib.aurora

import se.gustavkarlsson.skylight.android.core.entities.Report
import se.gustavkarlsson.skylight.android.lib.darkness.Darkness
import se.gustavkarlsson.skylight.android.lib.geomaglocation.GeomagLocation
import se.gustavkarlsson.skylight.android.lib.kpindex.KpIndexResult
import se.gustavkarlsson.skylight.android.lib.weather.Weather

data class CompleteAuroraReport(
    val kpIndex: KpIndexResult,
    val geomagLocation: GeomagLocation,
    val darkness: Darkness,
    val weather: Report<Weather>,
)
