package se.gustavkarlsson.skylight.android.lib.weather

import io.reactivex.Flowable
import io.reactivex.Single
import org.threeten.bp.Duration
import se.gustavkarlsson.koptional.Optional
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.entities.Report
import se.gustavkarlsson.skylight.android.entities.Weather
import se.gustavkarlsson.skylight.android.extensions.delay
import se.gustavkarlsson.skylight.android.services.Time
import se.gustavkarlsson.skylight.android.services.WeatherProvider
import timber.log.Timber

internal class RetrofittedOpenWeatherMapWeatherProvider(
	private val api: OpenWeatherMapApi,
	private val appId: String,
	private val time: Time,
	private val retryDelay: Duration,
	private val pollingInterval: Duration
) : WeatherProvider {

	private fun getReport(location: Location): Single<Report<Weather>> =
		api.get(location.latitude, location.longitude, "json", appId)
			.map { Report.success(Weather(it.clouds.percentage), time.now().blockingGet()) }
			.doOnError { Timber.w(it, "Failed to get Weather from OpenWeatherMap API") }

	override fun get(location: Single<Optional<Location>>): Single<Report<Weather>> {
		return location
			.flatMap { (maybeLocation) ->
				maybeLocation?.let { location ->
					getReport(location)
						.onErrorReturnItem(Report.error(R.string.error_no_internet_maybe, time.now().blockingGet()))
				} ?: Single.just(Report.error(R.string.error_no_location, time.now().blockingGet()))
			}
			.doOnSuccess { Timber.i("Provided weather: %s", it) }
	}

	override fun stream(locations: Flowable<Optional<Location>>): Flowable<Report<Weather>> =
		locations
			.switchMap { (maybeLocation) ->
				maybeLocation?.let { location ->
					getReport(location)
						.repeatWhen { it.delay(pollingInterval) }
						.onErrorResumeNext { it: Throwable ->
							Flowable.error<Report<Weather>>(it)
								.startWith(Report.error(R.string.error_no_internet_maybe, time.now().blockingGet()))
						}
						.retryWhen { it.delay(retryDelay) }
				} ?: Flowable.just(Report.error(R.string.error_no_location, time.now().blockingGet()))
			}
			.distinctUntilChanged()
			.doOnNext { Timber.i("Streamed weather: %s", it) }
			.replay(1)
			.refCount()
}
