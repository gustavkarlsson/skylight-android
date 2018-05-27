package se.gustavkarlsson.skylight.android.services_impl.providers

import com.hadisatrio.optional.Optional
import io.reactivex.Single
import io.reactivex.functions.Function6
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.entities.*
import se.gustavkarlsson.skylight.android.services.providers.*
import timber.log.Timber

class CombiningAuroraReportProvider(
	private val timeProvider: TimeProvider,
	private val locationProvider: LocationProvider,
	private val locationNameProvider: LocationNameProvider,
	private val darknessProvider: DarknessProvider,
	private val geomagLocationProvider: GeomagLocationProvider,
	private val kpIndexProvider: KpIndexProvider,
	private val weatherProvider: WeatherProvider
) : AuroraReportProvider {

	override fun get(): Single<AuroraReport> {
		return Single.fromCallable {
			val time = timeProvider.getTime().cache()
			val location = locationProvider.get().cache()
			zipToAuroraReport(time, location)
		}
			.flatMap { it }
			.doOnSuccess { Timber.i("Provided aurora report: %s", it) }
	}

	private fun zipToAuroraReport(
		time: Single<Instant>,
		location: Single<Optional<Location>>
	): Single<AuroraReport> {
		return Single.zip(
			time,
			locationNameProvider.get(location),
			kpIndexProvider.get(),
			geomagLocationProvider.get(location),
			darknessProvider.get(time, location),
			weatherProvider.get(location),
			Function6 { theTime: Instant,
						locationName: Optional<String>,
						kpIndex: KpIndex,
						geomagLocation: GeomagLocation,
						darkness: Darkness,
						weather: Weather
				->
				val factors = AuroraFactors(kpIndex, geomagLocation, darkness, weather)
				AuroraReport(theTime, locationName.orNull(), factors)
			})
	}
}
