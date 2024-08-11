package se.gustavkarlsson.skylight.android.lib.aurora

import dagger.Reusable
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.core.entities.Loading
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import se.gustavkarlsson.skylight.android.lib.darkness.DarknessForecastProvider
import se.gustavkarlsson.skylight.android.lib.geomaglocation.GeomagLocationProvider
import se.gustavkarlsson.skylight.android.lib.kpindex.KpIndexForecastProvider
import se.gustavkarlsson.skylight.android.lib.location.Location
import se.gustavkarlsson.skylight.android.lib.weather.WeatherForecastProvider
import javax.inject.Inject

@Reusable
internal class CombiningAuroraForecastReportProvider @Inject constructor(
    private val darknessForecastProvider: DarknessForecastProvider,
    private val geomagLocationProvider: GeomagLocationProvider,
    private val kpIndexForecastProvider: KpIndexForecastProvider,
    private val weatherForecastProvider: WeatherForecastProvider,
) : AuroraForecastReportProvider {
    override suspend fun get(location: Location): AuroraForecastReport =
        coroutineScope {
            val kpIndex = async { kpIndexForecastProvider.get() }
            val geomagLocation = geomagLocationProvider.get(location)
            val darkness = darknessForecastProvider.get(location)
            val weather = async { weatherForecastProvider.get(location) }
            val report = AuroraForecastReport(
                location,
                kpIndex.await(),
                geomagLocation,
                darkness,
                weather.await(),
            )
            logInfo { "Provided aurora forecast report: $report" }
            report
        }

    override fun stream(location: Location): Flow<Loadable<AuroraForecastReport>> {
        val stream = streamWithLocation(location)
        return stream
            .distinctUntilChanged()
            .onEach { logInfo { "Streamed aurora forecast report: $it" } }
    }

    private fun streamWithLocation(location: Location): Flow<Loadable<AuroraForecastReport>> {
        val geomagLocation = geomagLocationProvider.get(location)
        return combine(
            kpIndexForecastProvider.stream().map { it.getOrNull() },
            darknessForecastProvider.stream(location),
            weatherForecastProvider.stream(location).map { it.getOrNull() },
        ) { kpIndex, darkness, weather ->
            if (kpIndex != null && weather != null) {
                Loadable(AuroraForecastReport(location, kpIndex, geomagLocation, darkness, weather))
            } else {
                Loading
            }
        }
    }
}
