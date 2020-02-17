package se.gustavkarlsson.skylight.android.feature.main

import com.jakewharton.rx.replayingShare
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.Singles
import se.gustavkarlsson.skylight.android.entities.CompleteAuroraReport
import se.gustavkarlsson.skylight.android.entities.Loadable
import se.gustavkarlsson.skylight.android.entities.LoadableAuroraReport
import se.gustavkarlsson.skylight.android.entities.LocationResult
import se.gustavkarlsson.skylight.android.services.AuroraReportProvider
import se.gustavkarlsson.skylight.android.services.DarknessProvider
import se.gustavkarlsson.skylight.android.services.GeomagLocationProvider
import se.gustavkarlsson.skylight.android.services.KpIndexProvider
import se.gustavkarlsson.skylight.android.services.ReverseGeocoder
import se.gustavkarlsson.skylight.android.services.WeatherProvider
import timber.log.Timber

internal class CombiningAuroraReportProvider(
    private val reverseGeocoder: ReverseGeocoder,
    private val darknessProvider: DarknessProvider,
    private val geomagLocationProvider: GeomagLocationProvider,
    private val kpIndexProvider: KpIndexProvider,
    private val weatherProvider: WeatherProvider
) : AuroraReportProvider {
    override fun get(location: Single<LocationResult>): Single<CompleteAuroraReport> =
        zipToAuroraReport(location)
            .doOnSuccess { Timber.i("Provided aurora report: %s", it) }

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
            .doOnNext { Timber.i("Streamed aurora report: %s", it) }
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
