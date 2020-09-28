package se.gustavkarlsson.skylight.android.lib.aurora

import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.core.entities.Report
import se.gustavkarlsson.skylight.android.lib.darkness.Darkness
import se.gustavkarlsson.skylight.android.lib.geomaglocation.GeomagLocation
import se.gustavkarlsson.skylight.android.lib.kpindex.KpIndex
import se.gustavkarlsson.skylight.android.lib.reversegeocoder.ReverseGeocodingResult
import se.gustavkarlsson.skylight.android.lib.weather.Weather

data class LoadableAuroraReport(
    val locationName: Loadable<ReverseGeocodingResult>,
    val kpIndex: Loadable<Report<KpIndex>>,
    val geomagLocation: Loadable<Report<GeomagLocation>>,
    val darkness: Loadable<Report<Darkness>>,
    val weather: Loadable<Report<Weather>>
) {
    val timestamp: Instant?
        get() = listOf(kpIndex, geomagLocation, darkness, weather)
            .flatMap {
                if (it is Loadable.Loaded)
                    listOf(it.value.timestamp)
                else
                    emptyList()
            }
            .maxOrNull()

    fun toCompleteAuroraReport(): CompleteAuroraReport? =
        if (locationName is Loadable.Loaded &&
            kpIndex is Loadable.Loaded &&
            geomagLocation is Loadable.Loaded &&
            darkness is Loadable.Loaded &&
            weather is Loadable.Loaded
        ) {
            CompleteAuroraReport(
                locationName = locationName.value,
                kpIndex = kpIndex.value,
                geomagLocation = geomagLocation.value,
                darkness = darkness.value,
                weather = weather.value
            )
        } else null

    companion object {
        val LOADING = LoadableAuroraReport(
            Loadable.Loading,
            Loadable.Loading,
            Loadable.Loading,
            Loadable.Loading,
            Loadable.Loading
        )
    }
}
