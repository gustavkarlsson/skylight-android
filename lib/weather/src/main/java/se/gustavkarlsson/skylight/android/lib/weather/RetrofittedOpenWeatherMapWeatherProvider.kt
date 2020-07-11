package se.gustavkarlsson.skylight.android.lib.weather

import com.jakewharton.rx.replayingShare
import io.reactivex.Observable
import io.reactivex.Single
import java.io.IOException
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.entities.Cause
import se.gustavkarlsson.skylight.android.entities.Loadable
import se.gustavkarlsson.skylight.android.entities.Report
import se.gustavkarlsson.skylight.android.lib.location.Location
import se.gustavkarlsson.skylight.android.lib.location.LocationResult
import se.gustavkarlsson.skylight.android.lib.time.Time
import se.gustavkarlsson.skylight.android.utils.delay
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
            .doOnError { Timber.w(it, "Failed to get Weather from OpenWeatherMap API") }
            .flatMap<Report<Weather>> { response ->
                if (response.isSuccessful) {
                    Single.just(Report.Success(Weather(response.body()!!.clouds.all), time.now()))
                } else {
                    val exception = ServerResponseException(response.code(), response.errorBody()!!.string())
                    Timber.e(exception, "Failed to get Weather from OpenWeatherMap API")
                    Single.error(exception)
                }
            }
}

// TODO Fix duplication with RetrofittedKpIndexProvider
private fun getCause(throwable: Throwable): Cause =
    when (throwable) {
        is IOException -> Cause.Connectivity
        is ServerResponseException -> Cause.ServerResponse
        else -> Cause.Unknown
    }

// TODO Fix duplication with RetrofittedKpIndexProvider
private class ServerResponseException(code: Int, body: String) : Exception("Server error $code. Body: $body")
