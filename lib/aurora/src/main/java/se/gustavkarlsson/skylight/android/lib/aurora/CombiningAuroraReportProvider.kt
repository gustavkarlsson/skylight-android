package se.gustavkarlsson.skylight.android.lib.aurora

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import se.gustavkarlsson.skylight.android.lib.darkness.DarknessProvider
import se.gustavkarlsson.skylight.android.lib.geomaglocation.GeomagLocationProvider
import se.gustavkarlsson.skylight.android.lib.kpindex.KpIndexProvider
import se.gustavkarlsson.skylight.android.lib.location.LocationResult
import se.gustavkarlsson.skylight.android.lib.reversegeocoder.ReverseGeocoder
import se.gustavkarlsson.skylight.android.lib.weather.WeatherProvider

internal class CombiningAuroraReportProvider(
    private val reverseGeocoder: ReverseGeocoder,
    private val darknessProvider: DarknessProvider,
    private val geomagLocationProvider: GeomagLocationProvider,
    private val kpIndexProvider: KpIndexProvider,
    private val weatherProvider: WeatherProvider
) : AuroraReportProvider {
    override suspend fun get(getLocation: suspend () -> LocationResult): CompleteAuroraReport =
        coroutineScope {
            val location = async { getLocation() }
            val locationName = async { reverseGeocoder.get(location.await()) }
            val kpIndex = async { kpIndexProvider.get() }
            val geomagLocation = async { geomagLocationProvider.get(location.await()) }
            val darkness = async { darknessProvider.get(location.await()) }
            val weather = async { weatherProvider.get(location.await()) }
            val report = CompleteAuroraReport(
                locationName.await(),
                kpIndex.await(),
                geomagLocation.await(),
                darkness.await(),
                weather.await()
            )
            logInfo { "Provided aurora report: $report" }
            report
        }

    @ExperimentalCoroutinesApi
    override fun stream(
        locations: Flow<Loadable<LocationResult>>
    ): Flow<LoadableAuroraReport> =
        combine(
            reverseGeocoder.stream(locations),
            kpIndexProvider.stream(),
            geomagLocationProvider.stream(locations),
            darknessProvider.stream(locations),
            weatherProvider.stream(locations)
        ) { locationName, kpIndex, geomagLocation, darkness, weather ->
            LoadableAuroraReport(locationName, kpIndex, geomagLocation, darkness, weather)
        }
            .distinctUntilChanged()
            .onEach { logInfo { "Streamed aurora report: $it" } }
}
