package se.gustavkarlsson.skylight.android.feature.main

import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.rxkotlin.Flowables
import io.reactivex.rxkotlin.Singles
import se.gustavkarlsson.koptional.Optional
import se.gustavkarlsson.koptional.toOptional
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.services.AuroraReportProvider
import se.gustavkarlsson.skylight.android.lib.darkness.DarknessProvider
import se.gustavkarlsson.skylight.android.lib.geomaglocation.GeomagLocationProvider
import se.gustavkarlsson.skylight.android.lib.kpindex.KpIndexProvider
import se.gustavkarlsson.skylight.android.lib.reversegeocoder.ReverseGeocoder
import se.gustavkarlsson.skylight.android.lib.location.LocationProvider
import se.gustavkarlsson.skylight.android.lib.weather.WeatherProvider
import timber.log.Timber

internal class CombiningAuroraReportProvider(
	private val locationProvider: LocationProvider,
	private val reverseGeocoder: ReverseGeocoder,
	private val darknessProvider: se.gustavkarlsson.skylight.android.lib.darkness.DarknessProvider,
	private val geomagLocationProvider: se.gustavkarlsson.skylight.android.lib.geomaglocation.GeomagLocationProvider,
	private val kpIndexProvider: KpIndexProvider,
	private val weatherProvider: WeatherProvider
) : AuroraReportProvider {
	override fun get(location: Location?): Single<AuroraReport> {
		val locationSingle = location?.let { Single.just(it.toOptional()) }
			?: locationProvider.get()
		return zipToAuroraReport(locationSingle)
			.doOnSuccess { Timber.i("Provided aurora report: %s", it) }
	}

	override fun stream(location: Location?): Flowable<AuroraReport> {
		val locationFlowable = location?.let { Flowable.just(it.toOptional()) }
			?: locationProvider.stream
		return Flowables
			.combineLatest(
				reverseGeocoder.stream(locationFlowable),
				kpIndexProvider.stream,
				geomagLocationProvider.stream(locationFlowable),
				darknessProvider.stream(locationFlowable),
				weatherProvider.stream(locationFlowable)
			) { locationName, kpIndex, geomagLocation, darkness, weather ->
				AuroraReport(locationName.value, kpIndex, geomagLocation, darkness, weather)
			}
			.distinctUntilChanged()
			.doOnNext { Timber.i("Streamed aurora report: %s", it) }
			.replay(1)
			.refCount()
	}

	private fun zipToAuroraReport(
		location: Single<Optional<Location>>
	): Single<AuroraReport> {
		val cachedLocation = location.cache()
		return Singles
			.zip(
				reverseGeocoder.get(cachedLocation),
				kpIndexProvider.get(),
				geomagLocationProvider.get(cachedLocation),
				darknessProvider.get(cachedLocation),
				weatherProvider.get(cachedLocation)
			) { locationName, kpIndex, geomagLocation, darkness, weather ->
				AuroraReport(locationName.value, kpIndex, geomagLocation, darkness, weather)
			}
	}
}
