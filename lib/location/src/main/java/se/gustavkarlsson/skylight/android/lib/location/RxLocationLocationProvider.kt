package se.gustavkarlsson.skylight.android.lib.location

import android.annotation.SuppressLint
import com.google.android.gms.location.LocationRequest
import com.jakewharton.rx.replayingShare
import com.patloew.rxlocation.FusedLocation
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.core.entities.Loadable
import se.gustavkarlsson.skylight.android.core.logging.logDebug
import se.gustavkarlsson.skylight.android.core.logging.logError
import se.gustavkarlsson.skylight.android.core.logging.logInfo
import se.gustavkarlsson.skylight.android.core.logging.logWarn
import se.gustavkarlsson.skylight.android.core.utils.delay
import se.gustavkarlsson.skylight.android.core.utils.throttleLatest
import se.gustavkarlsson.skylight.android.core.utils.timeout
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
            .map { LocationResult.success(Location(it.latitude, it.longitude)) }
            .onErrorReturn { exception ->
                when (exception) {
                    is SecurityException -> {
                        logWarn(exception) { "Missing location permission" }
                        LocationResult.Failure.MissingPermission
                    }
                    is TimeoutException -> {
                        logWarn(exception) { "Timed out while getting location" }
                        LocationResult.Failure.Unknown
                    }
                    else -> {
                        logError(exception) { "Failed to get location" }
                        LocationResult.Failure.Unknown
                    }
                }
            }
            .doOnSuccess { logInfo { "Provided location: $it" } }

    @SuppressLint("MissingPermission")
    private val lastLocation = fusedLocation
        .lastLocation()
        .doOnSuccess { logDebug { "Last location: $it" } }

    @SuppressLint("MissingPermission")
    private val forcedLocation = fusedLocation
        .updates(forcedLocationRequest)
        .firstOrError()
        .doOnSuccess { logDebug { "Forced location: $it" } }

    @SuppressLint("MissingPermission")
    private val pollingLocations = fusedLocation
        .updates(pollingLocationRequest)
        .doOnNext { logDebug { "Polled location: $it" } }

    private val locations = lastLocation
        .switchIfEmpty(forcedLocation)
        .toObservable()
        .concatWith(pollingLocations)

    private val stream = locations
        .subscribeOn(Schedulers.io())
        .map { Location(it.latitude, it.longitude) }
        .distinctUntilChanged()
        .throttleLatest(throttleDuration)
        .map(LocationResult.Companion::success)
        .onErrorResumeNext { exception: Throwable ->
            val result = when (exception) {
                is SecurityException -> {
                    logWarn(exception) { "Missing location permission" }
                    LocationResult.Failure.MissingPermission
                }
                is TimeoutException -> {
                    logWarn(exception) { "Timed out while getting location" }
                    LocationResult.Failure.Unknown
                }
                else -> {
                    logError(exception) { "Failed to get location" }
                    LocationResult.Failure.Unknown
                }
            }
            Observable.concat(
                Observable.just(result),
                Observable.error(exception)
            )
        }
        .map { Loadable.loaded(it) }
        .retryWhen { it.delay(retryDelay) }
        .doOnNext { logInfo { "Streamed location: $it" } }
        .replayingShare(Loadable.Loading)

    override fun stream() = stream
}
