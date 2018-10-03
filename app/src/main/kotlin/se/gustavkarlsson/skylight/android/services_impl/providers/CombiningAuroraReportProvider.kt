package se.gustavkarlsson.skylight.android.services_impl.providers

import io.reactivex.Single
import io.reactivex.rxkotlin.Singles
import se.gustavkarlsson.koptional.Optional
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.services.providers.AuroraReportProvider
import se.gustavkarlsson.skylight.android.services.providers.DarknessProvider
import se.gustavkarlsson.skylight.android.services.providers.GeomagLocationProvider
import se.gustavkarlsson.skylight.android.services.providers.KpIndexProvider
import se.gustavkarlsson.skylight.android.services.providers.LocationNameProvider
import se.gustavkarlsson.skylight.android.services.providers.LocationProvider
import se.gustavkarlsson.skylight.android.services.providers.WeatherProvider
import timber.log.Timber

class CombiningAuroraReportProvider(
	private val locationProvider: LocationProvider,
	private val locationNameProvider: LocationNameProvider,
	private val darknessProvider: DarknessProvider,
	private val geomagLocationProvider: GeomagLocationProvider,
	private val kpIndexProvider: KpIndexProvider,
	private val weatherProvider: WeatherProvider
) : AuroraReportProvider {

	override fun get(): Single<AuroraReport> {
		return Single.fromCallable {
			val location = locationProvider.get().cache()
			zipToAuroraReport(location)
		}
			.flatMap { it }
			.doOnSuccess { Timber.i("Provided aurora report: %s", it) }
	}

	private fun zipToAuroraReport(
		location: Single<Optional<Location>>
	): Single<AuroraReport> {
		return Singles.zip(
			locationNameProvider.get(location),
			kpIndexProvider.get(),
			geomagLocationProvider.get(location),
			darknessProvider.get(location),
			weatherProvider.get(location)
		) { locationName, kpIndex, geomagLocation, darkness, weather ->
			AuroraReport(locationName.value, kpIndex, geomagLocation, darkness, weather)
		}
	}
}
