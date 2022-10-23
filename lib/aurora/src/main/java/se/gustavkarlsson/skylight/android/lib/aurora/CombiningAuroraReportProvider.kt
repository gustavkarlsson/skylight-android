package se.gustavkarlsson.skylight.android.lib.aurora

import dagger.Reusable
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import se.gustavkarlsson.skylight.android.core.entities.Loaded
import se.gustavkarlsson.skylight.android.core.entities.Loading
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import se.gustavkarlsson.skylight.android.lib.darkness.DarknessProvider
import se.gustavkarlsson.skylight.android.lib.geomaglocation.GeomagLocationProvider
import se.gustavkarlsson.skylight.android.lib.kpindex.KpIndexProvider
import se.gustavkarlsson.skylight.android.lib.location.Location
import se.gustavkarlsson.skylight.android.lib.weather.WeatherProvider
import javax.inject.Inject

@Reusable
internal class CombiningAuroraReportProvider @Inject constructor(
    private val darknessProvider: DarknessProvider,
    private val geomagLocationProvider: GeomagLocationProvider,
    private val kpIndexProvider: KpIndexProvider,
    private val weatherProvider: WeatherProvider,
) : AuroraReportProvider {
    override suspend fun get(location: Location): AuroraReport =
        coroutineScope {
            val kpIndex = async { kpIndexProvider.get() }
            val geomagLocation = geomagLocationProvider.get(location)
            val darkness = darknessProvider.get(location)
            val weather = async { weatherProvider.get(location) }
            val report = AuroraReport(
                location,
                kpIndex.await(),
                geomagLocation,
                darkness,
                weather.await(),
            )
            logInfo { "Provided aurora report: $report" }
            report
        }

    override fun stream(location: Location?): Flow<LoadableAuroraReport> {
        val stream = if (location != null) {
            streamWithLocation(location)
        } else {
            streamWithoutLocation()
        }
        return stream
            .distinctUntilChanged()
            .onEach { logInfo { "Streamed aurora report: $it" } }
    }

    private fun streamWithLocation(location: Location): Flow<LoadableAuroraReport> {
        val geomagLocation = Loaded(geomagLocationProvider.get(location))
        return combine(
            kpIndexProvider.stream(),
            darknessProvider.stream(location),
            weatherProvider.stream(location),
        ) { kpIndex, darkness, weather ->
            LoadableAuroraReport(location, kpIndex, geomagLocation, Loaded(darkness), weather)
        }
    }

    private fun streamWithoutLocation(): Flow<LoadableAuroraReport> {
        return kpIndexProvider.stream().map { kpIndex ->
            LoadableAuroraReport(null, kpIndex, Loading, Loading, Loading)
        }
    }
}
