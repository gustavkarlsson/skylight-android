package se.gustavkarlsson.skylight.android.lib.location

import android.annotation.SuppressLint
import com.google.android.gms.location.LocationRequest
import com.jakewharton.rx.replayingShare
import com.patloew.rxlocation.FusedLocation
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.entities.Loadable
import se.gustavkarlsson.skylight.android.utils.delay
import se.gustavkarlsson.skylight.android.utils.timeout
import timber.log.Timber
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

internal class RxLocationLocationProvider(
    private val fusedLocation: FusedLocation,
    private val timeout: Duration,
    requestAccuracy: Int,
    throttleDuration: Duration,
    firstPollingInterval: Duration,
    restPollingInterval: Duration,
    retryDelay: Duration
) : LocationProvider {

    private val singleLocationRequest = LocationRequest().apply {
        priority = requestAccuracy
        interval = timeout.toMillis() / 2
        numUpdates = 1
    }

    private val forcedLocationRequest = LocationRequest().apply {
        priority = requestAccuracy
        interval = firstPollingInterval.toMillis()
        numUpdates = 1
    }

    private val pollingLocationRequest = LocationRequest().apply {
        priority = requestAccuracy
        interval = restPollingInterval.toMillis()
    }

    @SuppressLint("MissingPermission")
    override fun get(): Single<LocationResult> =
        fusedLocation
            .updates(singleLocationRequest)
            .subscribeOn(Schedulers.io())
            .firstOrError()
            .timeout(timeout)
            .map<LocationResult> { LocationResult.Success(Location(it.latitude, it.longitude)) }
            .onErrorReturn { exception ->
                when (exception) {
                    is SecurityException -> {
                        Timber.w(exception, "Missing location permission")
                        LocationResult.Failure.MissingPermission
                    }
                    is TimeoutException -> {
                        Timber.w(exception, "Timed out while getting location")
                        LocationResult.Failure.Unknown
                    }
                    else -> {
                        Timber.e(exception, "Failed to get location")
                        LocationResult.Failure.Unknown
                    }
                }
            }
            .doOnSuccess { Timber.i("Provided location: %s", it) }

    @SuppressLint("MissingPermission")
    private val lastLocation = fusedLocation
        .lastLocation()
        .doOnSuccess { Timber.d("Last location: %s", it) }

    @SuppressLint("MissingPermission")
    private val forcedLocation = fusedLocation
        .updates(forcedLocationRequest)
        .firstOrError()
        .doOnSuccess { Timber.d("Forced location: %s", it) }

    @SuppressLint("MissingPermission")
    private val pollingLocations = fusedLocation
        .updates(pollingLocationRequest)
        .doOnNext { Timber.d("Polled location: %s", it) }

    private val locations = lastLocation
        .switchIfEmpty(forcedLocation)
        .toObservable()
        .concatWith(pollingLocations)

    private val stream = locations
        .subscribeOn(Schedulers.io())
        .map { Location(it.latitude, it.longitude) }
        .distinctUntilChanged()
        .throttleLatest(throttleDuration.toMillis(), TimeUnit.MILLISECONDS)
        .map<LocationResult>(LocationResult::Success)
        .onErrorResumeNext { exception: Throwable ->
            val result = when (exception) {
                is SecurityException -> {
                    Timber.w(exception, "Missing location permission")
                    LocationResult.Failure.MissingPermission
                }
                is TimeoutException -> {
                    Timber.w(exception, "Timed out while getting location")
                    LocationResult.Failure.Unknown
                }
                else -> {
                    Timber.e(exception, "Failed to get location")
                    LocationResult.Failure.Unknown
                }
            }
            Observable.concat(
                Observable.just(result),
                Observable.error(exception)
            )
        }
        .map<Loadable<LocationResult>> { Loadable.Loaded(it) }
        .retryWhen { it.delay(retryDelay) }
        .doOnNext { Timber.i("Streamed location: %s", it) }
        .replayingShare(Loadable.Loading)

    override fun stream() = stream
}
