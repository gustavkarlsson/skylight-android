package se.gustavkarlsson.skylight.android.lib.aurora

import com.jakewharton.rx.replayingShare
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.Singles
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
    override fun get(location: Single<LocationResult>): Single<CompleteAuroraReport> =
        zipToAuroraReport(location)
            .doOnSuccess { logInfo { "Provided aurora report: $it" } }

    override fun stream(
        locations: Observable<Loadable<LocationResult>>
    ): Observable<LoadableAuroraReport> =
        Observables
            .combineLatest(
                reverseGeocoder.stream(locations),
                kpIndexProvider.stream(),
                geomagLocationProvider.stream(locations),
                darknessProvider.stream(locations),
                weatherProvider.stream(locations)
            ) { locationName, kpIndex, geomagLocation, darkness, weather ->
                LoadableAuroraReport(locationName, kpIndex, geomagLocation, darkness, weather)
            }
            .distinctUntilChanged()
            .doOnNext { logInfo { "Streamed aurora report: $it" } }
            .replayingShare(LoadableAuroraReport.LOADING)

    private fun zipToAuroraReport(location: Single<LocationResult>): Single<CompleteAuroraReport> {
        val cachedLocation = location.cache()
        return Singles
            .zip(
                reverseGeocoder.get(cachedLocation),
                kpIndexProvider.get(),
                geomagLocationProvider.get(cachedLocation),
                darknessProvider.get(cachedLocation),
                weatherProvider.get(cachedLocation)
            ) { locationName, kpIndex, geomagLocation, darkness, weather ->
                CompleteAuroraReport(locationName, kpIndex, geomagLocation, darkness, weather)
            }
    }
}
