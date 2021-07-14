package se.gustavkarlsson.skylight.android.lib.aurora

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import se.gustavkarlsson.skylight.android.core.entities.Loaded
import se.gustavkarlsson.skylight.android.core.entities.Report
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import se.gustavkarlsson.skylight.android.lib.darkness.DarknessProvider
import se.gustavkarlsson.skylight.android.lib.geomaglocation.GeomagLocationProvider
import se.gustavkarlsson.skylight.android.lib.kpindex.KpIndexProvider
import se.gustavkarlsson.skylight.android.lib.location.Location
import se.gustavkarlsson.skylight.android.lib.time.Time
import se.gustavkarlsson.skylight.android.lib.weather.WeatherProvider

internal class CombiningAuroraReportProvider(
    private val darknessProvider: DarknessProvider,
    private val geomagLocationProvider: GeomagLocationProvider,
    private val kpIndexProvider: KpIndexProvider,
    private val weatherProvider: WeatherProvider,
    private val time: Time,
) : AuroraReportProvider {
    override suspend fun get(location: Location): CompleteAuroraReport =
        coroutineScope {
            val kpIndex = async { kpIndexProvider.get() }
            val geomagLocation = async { geomagLocationProvider.get(location) }
            val darkness = async { darknessProvider.get(location) }
            val weather = async { weatherProvider.get(location) }
            val report = CompleteAuroraReport(
                kpIndex.await(),
                geomagLocation.await(),
                darkness.await(),
                weather.await()
            )
            logInfo { "Provided aurora report: $report" }
            report
        }

    override fun stream(location: Location): Flow<LoadableAuroraReport> {
        // FIXME don't wrap in report. Also remove time from deps
        val geomagLocation = Loaded(Report.success(geomagLocationProvider.getNew(location), time.now()))
        return combine(
            kpIndexProvider.stream(),
            darknessProvider.stream(location),
            weatherProvider.stream(location)
        ) { kpIndex, darkness, weather ->
            LoadableAuroraReport(kpIndex, geomagLocation, darkness, weather)
        }
            .distinctUntilChanged()
            .onEach { logInfo { "Streamed aurora report: $it" } }
    }
}
