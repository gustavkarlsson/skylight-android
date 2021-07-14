package se.gustavkarlsson.skylight.android.lib.aurora

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import se.gustavkarlsson.skylight.android.core.entities.Loading
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import se.gustavkarlsson.skylight.android.lib.darkness.DarknessProvider
import se.gustavkarlsson.skylight.android.lib.geomaglocation.GeomagLocationProvider
import se.gustavkarlsson.skylight.android.lib.kpindex.KpIndexProvider
import se.gustavkarlsson.skylight.android.lib.location.Location
import se.gustavkarlsson.skylight.android.lib.location.LocationResult
import se.gustavkarlsson.skylight.android.lib.weather.WeatherProvider

internal class CombiningAuroraReportProvider(
    private val darknessProvider: DarknessProvider,
    private val geomagLocationProvider: GeomagLocationProvider,
    private val kpIndexProvider: KpIndexProvider,
    private val weatherProvider: WeatherProvider
) : AuroraReportProvider {
    override suspend fun get(getLocation: suspend () -> LocationResult): CompleteAuroraReport =
        coroutineScope {
            val location = async { getLocation() }
            val kpIndex = async { kpIndexProvider.get() }
            val geomagLocation = async { geomagLocationProvider.get(location.await()) }
            val darkness = async { darknessProvider.get(location.await()) }
            val weather = async { weatherProvider.get(location.await()) }
            val report = CompleteAuroraReport(
                kpIndex.await(),
                geomagLocation.await(),
                darkness.await(),
                weather.await()
            )
            logInfo { "Provided aurora report: $report" }
            report
        }

    override fun stream(location: Location): Flow<LoadableAuroraReport> =
        combine(
            kpIndexProvider.stream(),
            geomagLocationProvider.stream(location),
            darknessProvider.stream(location),
            weatherProvider.stream(location)
        ) { kpIndex, geomagLocation, darkness, weather ->
            LoadableAuroraReport(kpIndex, geomagLocation, darkness, weather)
        }
            .onStart {
                LoadableAuroraReport(Loading, Loading, Loading, Loading)
            }
            .distinctUntilChanged()
            .onEach { logInfo { "Streamed aurora report: $it" } }
}
