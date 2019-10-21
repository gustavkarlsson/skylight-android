package se.gustavkarlsson.skylight.android.lib.weather

import com.jakewharton.rx.replayingShare
import io.reactivex.Flowable
import io.reactivex.Single
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.entities.Loadable
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.entities.LocationResult
import se.gustavkarlsson.skylight.android.entities.Report
import se.gustavkarlsson.skylight.android.entities.Weather
import se.gustavkarlsson.skylight.android.extensions.delay
import se.gustavkarlsson.skylight.android.services.Time
import timber.log.Timber

internal class RetrofittedOpenWeatherMapWeatherProvider(
	private val api: OpenWeatherMapApi,
	private val appId: String,
	private val time: Time,
	private val retryDelay: Duration,
	private val pollingInterval: Duration
) : WeatherProvider {

	override fun get(location: Single<LocationResult>): Single<Report<Weather>> =
		location
			.flatMap { result ->
				result.map(
					onSuccess = {
						getReport(it)
							.onErrorReturnItem(Report.error(R.string.error_no_internet_maybe, time.now().blockingGet()))
					},
					onMissingPermissionError = {
						Single.just(Report.error(R.string.error_no_location_permission, time.now().blockingGet()))
					},
					onUnknownError = {
						Single.just(Report.error(R.string.error_no_location, time.now().blockingGet()))
					}
				)
			}
			.doOnSuccess { Timber.i("Provided weather: %s", it) }

	override fun stream(
		locations: Flowable<Loadable<LocationResult>>
	): Flowable<Loadable<Report<Weather>>> =
		locations
			.switchMap { loadable ->
				when (loadable) {
					Loadable.Loading -> Flowable.just(Loadable.Loading)
					is Loadable.Loaded -> {
						val reports = loadable.value.map(
							onSuccess = ::streamReports,
							onMissingPermissionError = {
								Flowable.just(
									Report.error(
										R.string.error_no_location_permission,
										time.now().blockingGet()
									)
								)
							},
							onUnknownError = {
								Flowable.just(
									Report.error(
										R.string.error_no_location,
										time.now().blockingGet()
									)
								)
							}
						)
						reports.map { Loadable.Loaded(it) }
					}
				}
			}
			.distinctUntilChanged()
			.doOnNext { Timber.i("Streamed weather: %s", it) }
			.replayingShare(Loadable.Loading)

	private fun streamReports(location: Location): Flowable<Report<Weather>> =
		getReport(location)
			.repeatWhen { it.delay(pollingInterval) }
			.onErrorResumeNext { e: Throwable ->
				Flowable.concat(
					Flowable.just(
						Report.error(
							R.string.error_no_internet_maybe,
							time.now().blockingGet()
						)
					),
					Flowable.error<Report<Weather>>(e)
				)
			}
			.retryWhen { it.delay(retryDelay) }

	private fun getReport(location: Location): Single<Report<Weather>> =
		api.get(location.latitude, location.longitude, "json", appId)
			.map { Report.success(Weather(it.clouds.percentage), time.now().blockingGet()) }
			.doOnError { Timber.w(it, "Failed to get Weather from OpenWeatherMap API") }
}
