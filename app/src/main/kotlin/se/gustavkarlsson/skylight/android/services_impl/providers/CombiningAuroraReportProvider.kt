package se.gustavkarlsson.skylight.android.services_impl.providers

import com.hadisatrio.optional.Optional
import io.reactivex.Single
import io.reactivex.functions.Function5
import se.gustavkarlsson.skylight.android.entities.*
import se.gustavkarlsson.skylight.android.services.providers.*
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
		return Single.zip(
			locationNameProvider.get(location),
			kpIndexProvider.get(),
			geomagLocationProvider.get(location),
			darknessProvider.get(location),
			weatherProvider.get(location),
			Function5 { locationName: Optional<String>,
						kpIndex: Report<KpIndex>,
						geomagLocation: Report<GeomagLocation>,
						darkness: Report<Darkness>,
						weather: Report<Weather>
				->
				AuroraReport(locationName.orNull(), kpIndex, geomagLocation, darkness, weather)
			})
	}
}
