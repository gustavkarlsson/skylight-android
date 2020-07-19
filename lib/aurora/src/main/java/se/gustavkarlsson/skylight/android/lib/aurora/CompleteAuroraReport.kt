package se.gustavkarlsson.skylight.android.lib.aurora

import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.core.entities.Report
import se.gustavkarlsson.skylight.android.lib.darkness.Darkness
import se.gustavkarlsson.skylight.android.lib.geomaglocation.GeomagLocation
import se.gustavkarlsson.skylight.android.lib.kpindex.KpIndex
import se.gustavkarlsson.skylight.android.lib.reversegeocoder.ReverseGeocodingResult
import se.gustavkarlsson.skylight.android.lib.weather.Weather

data class CompleteAuroraReport(
    val locationName: ReverseGeocodingResult,
    val kpIndex: Report<KpIndex>,
    val geomagLocation: Report<GeomagLocation>,
    val darkness: Report<Darkness>,
    val weather: Report<Weather>
) {
    val timestamp: Instant
        get() = listOf(kpIndex, geomagLocation, darkness, weather)
            .map { it.timestamp }
            .max()!!
}
