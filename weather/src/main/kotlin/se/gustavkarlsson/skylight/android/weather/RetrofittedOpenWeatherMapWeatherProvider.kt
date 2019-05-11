package se.gustavkarlsson.skylight.android.weather

import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.Duration
import se.gustavkarlsson.koptional.Optional
import se.gustavkarlsson.koptional.optionalOf
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.entities.Report
import se.gustavkarlsson.skylight.android.entities.Weather
import se.gustavkarlsson.skylight.android.extensions.delay
import se.gustavkarlsson.skylight.android.services.providers.Time
import se.gustavkarlsson.skylight.android.services.providers.WeatherProvider
import timber.log.Timber

internal class RetrofittedOpenWeatherMapWeatherProvider(
	private val api: OpenWeatherMapApi,
	private val appId: String,
	private val retryCount: Long,
	private val pollingInterval: Duration,
	private val time: Time
) : WeatherProvider {

	override fun get(location: Single<Optional<Location>>): Single<Report<Weather>> {
		return location
			.flatMap { maybeLocation ->
				maybeLocation.value?.let {
					api.get(it.latitude, it.longitude, "json", appId)
						.subscribeOn(Schedulers.io())
						.map { Report.success(Weather(it.clouds.percentage), time.now().blockingGet()) }
						.doOnError { Timber.w(it, "Failed to get Weather from OpenWeatherMap API") }
						.retry(retryCount)
						.doOnError { Timber.e(it, "Failed to get Weather from OpenWeatherMap API after retrying %d times", retryCount) }
						.onErrorReturnItem(Report.error(R.string.error_no_internet_maybe, time.now().blockingGet()))
				} ?: Single.just(Report.error(R.string.error_no_location, time.now().blockingGet()))
			}
			.doOnSuccess { Timber.i("Provided weather: %s", it) }
	}

	override fun stream(locations: Flowable<Optional<Location>>): Flowable<Report<Weather>> =
		locations
			.switchMap { location ->
				val maybeLocation = Single.just(location)
				get(maybeLocation)
					.repeatWhen { it.delay(pollingInterval) }
			}
			.distinctUntilChanged()
			.doOnNext { Timber.i("Streamed weather: %s", it) }
			.replay(1)
			.refCount()
}
