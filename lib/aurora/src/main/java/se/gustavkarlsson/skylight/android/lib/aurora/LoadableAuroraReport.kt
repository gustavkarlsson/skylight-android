package se.gustavkarlsson.skylight.android.lib.aurora

import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.core.entities.Loaded
import se.gustavkarlsson.skylight.android.core.entities.Loading
import se.gustavkarlsson.skylight.android.lib.darkness.Darkness
import se.gustavkarlsson.skylight.android.lib.geomaglocation.GeomagLocation
import se.gustavkarlsson.skylight.android.lib.kpindex.KpIndexResult
import se.gustavkarlsson.skylight.android.lib.location.Location
import se.gustavkarlsson.skylight.android.lib.weather.WeatherResult

data class LoadableAuroraReport(
    val location: Location?,
    val kpIndex: Loadable<KpIndexResult>,
    val geomagLocation: Loadable<GeomagLocation>,
    val darkness: Loadable<Darkness>,
    val weather: Loadable<WeatherResult>,
) {

    fun toCompleteAuroraReport(): CompleteAuroraReport? =
        if (location != null &&
            kpIndex is Loaded &&
            geomagLocation is Loaded &&
            darkness is Loaded &&
            weather is Loaded
        ) {
            CompleteAuroraReport(
                location = location,
                kpIndex = kpIndex.value,
                geomagLocation = geomagLocation.value,
                darkness = darkness.value,
                weather = weather.value,
            )
        } else null

    companion object {
        val LOADING = LoadableAuroraReport(
            null,
            Loading,
            Loading,
            Loading,
            Loading,
        )
    }
}
