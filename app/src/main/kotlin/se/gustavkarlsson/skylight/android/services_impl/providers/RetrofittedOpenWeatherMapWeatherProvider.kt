package se.gustavkarlsson.skylight.android.services_impl.providers

import com.hadisatrio.optional.Optional
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.entities.Weather
import se.gustavkarlsson.skylight.android.services.providers.WeatherProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.openweathermap.OpenWeatherMapApi
import timber.log.Timber

class RetrofittedOpenWeatherMapWeatherProvider constructor(
	private val api: OpenWeatherMapApi,
	private val appId: String,
	private val retryCount: Long = 5
) : WeatherProvider {

	override fun get(location: Single<Optional<Location>>): Single<Weather> {
		return location
			.flatMap {
				it.orNull()?.let {
					api.get(it.latitude, it.longitude, "json", appId)
						.subscribeOn(Schedulers.io())
						.map { Weather(it.clouds.percentage) }
						.doOnError { Timber.w(it, "Failed to get Weather from OpenWeatherMap API") }
						.retry(retryCount)
						.doOnError { Timber.e(it, "Failed to get Weather from OpenWeatherMap API after retrying $retryCount times") }
						.onErrorReturnItem(Weather())
				} ?: Single.just(Weather())
			}
			.doOnSuccess { Timber.i("Provided weather: %s", it) }
	}
}
