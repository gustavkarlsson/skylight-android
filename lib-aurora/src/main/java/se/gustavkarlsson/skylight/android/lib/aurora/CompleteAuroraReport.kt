package se.gustavkarlsson.skylight.android.lib.aurora

import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.entities.GeomagLocation
import se.gustavkarlsson.skylight.android.entities.KpIndex
import se.gustavkarlsson.skylight.android.entities.Report
import se.gustavkarlsson.skylight.android.entities.ReverseGeocodingResult
import se.gustavkarlsson.skylight.android.entities.Weather
import se.gustavkarlsson.skylight.android.lib.darkness.Darkness

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
