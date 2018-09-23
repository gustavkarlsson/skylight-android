package se.gustavkarlsson.skylight.android.services_impl.providers

import com.hadisatrio.optional.Optional
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.entities.Report
import se.gustavkarlsson.skylight.android.entities.Weather
import se.gustavkarlsson.skylight.android.services.providers.TimeProvider
import se.gustavkarlsson.skylight.android.services.providers.WeatherProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.openweathermap.OpenWeatherMapApi
import timber.log.Timber

class RetrofittedOpenWeatherMapWeatherProvider(
	private val api: OpenWeatherMapApi,
	private val appId: String,
	private val retryCount: Long,
	private val timeProvider: TimeProvider
) : WeatherProvider {

	override fun get(location: Single<Optional<Location>>): Single<Report<Weather>> {
		return location
			.flatMap { maybeLocation ->
				maybeLocation.orNull()?.let {
					api.get(it.latitude, it.longitude, "json", appId)
						.subscribeOn(Schedulers.io())
						.map { Report.success(Weather(it.clouds.percentage), timeProvider.getTime().blockingGet()) }
						.doOnError { Timber.w(it, "Failed to get Weather from OpenWeatherMap API") }
						.retry(retryCount)
						.doOnError { Timber.e(it, "Failed to get Weather from OpenWeatherMap API after retrying $retryCount times") }
						.onErrorReturnItem(Report.error(R.string.error_no_internet_maybe, timeProvider.getTime().blockingGet()))
				} ?: Single.just(Report.error(R.string.error_no_location, timeProvider.getTime().blockingGet()))
			}
			.doOnSuccess { Timber.i("Provided weather: %s", it) }
	}
}
