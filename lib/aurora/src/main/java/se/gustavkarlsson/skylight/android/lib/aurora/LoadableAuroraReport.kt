package se.gustavkarlsson.skylight.android.lib.aurora

import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.core.entities.Report
import se.gustavkarlsson.skylight.android.lib.darkness.Darkness
import se.gustavkarlsson.skylight.android.lib.geomaglocation.GeomagLocation
import se.gustavkarlsson.skylight.android.lib.kpindex.KpIndex
import se.gustavkarlsson.skylight.android.lib.weather.Weather

data class LoadableAuroraReport(
    val kpIndex: Loadable<Report<KpIndex>>,
    val geomagLocation: Loadable<Report<GeomagLocation>>,
    val darkness: Loadable<Report<Darkness>>,
    val weather: Loadable<Report<Weather>>
) {

    fun toCompleteAuroraReport(): CompleteAuroraReport? =
        if (kpIndex is Loadable.Loaded &&
            geomagLocation is Loadable.Loaded &&
            darkness is Loadable.Loaded &&
            weather is Loadable.Loaded
        ) {
            CompleteAuroraReport(
                kpIndex = kpIndex.value,
                geomagLocation = geomagLocation.value,
                darkness = darkness.value,
                weather = weather.value,
            )
        } else null

    companion object {
        val LOADING = LoadableAuroraReport(
            Loadable.Loading,
            Loadable.Loading,
            Loadable.Loading,
            Loadable.Loading,
        )
    }
}
