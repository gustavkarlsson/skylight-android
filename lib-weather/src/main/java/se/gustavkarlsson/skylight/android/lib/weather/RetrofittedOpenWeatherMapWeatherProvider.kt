package se.gustavkarlsson.skylight.android.lib.weather

import com.jakewharton.rx.replayingShare
import io.reactivex.Observable
import io.reactivex.Single
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.entities.Cause
import se.gustavkarlsson.skylight.android.entities.Loadable
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.entities.LocationResult
import se.gustavkarlsson.skylight.android.entities.Report
import se.gustavkarlsson.skylight.android.entities.Weather
import se.gustavkarlsson.skylight.android.extensions.delay
import se.gustavkarlsson.skylight.android.lib.time.Time
import timber.log.Timber
import java.io.IOException

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
                            .onErrorReturn { throwable ->
                                Report.Error(getCause(throwable), time.now())
                            }
                    },
                    onMissingPermissionError = {
                        Single.just(Report.Error(Cause.LocationPermission, time.now()))
                    },
                    onUnknownError = {
                        Single.just(Report.Error(Cause.Location, time.now()))
                    }
                )
            }
            .doOnSuccess { Timber.i("Provided weather: %s", it) }

    override fun stream(
        locations: Observable<Loadable<LocationResult>>
    ): Observable<Loadable<Report<Weather>>> =
        locations
            .switchMap { loadable ->
                when (loadable) {
                    Loadable.Loading -> Observable.just(Loadable.Loading)
                    is Loadable.Loaded -> {
                        val reports = loadable.value.map(
                            onSuccess = ::streamReports,
                            onMissingPermissionError = {
                                Observable.just(
                                    Report.Error(Cause.LocationPermission, time.now())
                                )
                            },
                            onUnknownError = {
                                Observable.just(
                                    Report.Error(Cause.Location, time.now())
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

    private fun streamReports(location: Location): Observable<Report<Weather>> =
        getReport(location)
            .repeatWhen { it.delay(pollingInterval) }
            .toObservable()
            .onErrorResumeNext { throwable: Throwable ->
                Observable.concat(
                    Observable.just(Report.Error(getCause(throwable), time.now())),
                    Observable.error<Report<Weather>>(throwable)
                )
            }
            .retryWhen { it.delay(retryDelay) }

    private fun getReport(location: Location): Single<Report<Weather>> =
        api.get(location.latitude, location.longitude, "json", appId)
            .map<Report<Weather>> { Report.Success(Weather(it.clouds.all), time.now()) }
            .doOnError { Timber.w(it, "Failed to get Weather from OpenWeatherMap API") }
}

private fun getCause(throwable: Throwable): Cause =
    when (throwable) {
        is IOException -> Cause.Connectivity
        else -> Cause.Unknown
    }
