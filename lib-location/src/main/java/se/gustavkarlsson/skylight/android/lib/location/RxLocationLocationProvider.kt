package se.gustavkarlsson.skylight.android.lib.location

import android.annotation.SuppressLint
import com.google.android.gms.location.LocationRequest
import com.jakewharton.rx.replayingShare
import com.patloew.rxlocation.FusedLocation
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.Duration
import se.gustavkarlsson.koptional.Absent
import se.gustavkarlsson.koptional.Optional
import se.gustavkarlsson.koptional.optionalOf
import se.gustavkarlsson.koptional.toOptional
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.extensions.delay
import se.gustavkarlsson.skylight.android.extensions.timeout
import timber.log.Timber
import java.util.concurrent.TimeUnit

internal class RxLocationLocationProvider(
	private val fusedLocation: FusedLocation,
	private val timeout: Duration,
	requestAccuracy: Int,
	throttleDuration: Duration,
	firstPollingInterval: Duration,
	restPollingInterval: Duration,
	retryDelay: Duration
) : LocationProvider {

	private val locationRequest = LocationRequest().apply {
		priority = requestAccuracy
		interval = timeout.toMillis() / 2
		numUpdates = 1
	}

	@SuppressLint("MissingPermission")
	override fun get(): Single<Optional<Location>> =
		fusedLocation
			.updates(locationRequest)
			.subscribeOn(Schedulers.io())
			.firstOrError()
			.map { optionalOf(Location(it.latitude, it.longitude)) }
			.timeout(timeout)
			.doOnError { Timber.w(it, "Failed to get location") }
			.onErrorReturnItem(Absent)
			.doOnSuccess { Timber.i("Provided location: %s", it) }

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
		.toFlowable(BackpressureStrategy.LATEST)

	private val locations = lastLocation
		.switchIfEmpty(forcedLocation)
		.toFlowable()
		.concatWith(pollingLocations)

	override val stream: Flowable<Optional<Location>> = locations
		.subscribeOn(Schedulers.io())
		.distinctUntilChanged()
		.doOnError { Timber.w(it) }
		.retryWhen { it.delay(retryDelay) }
		.map { Location(it.latitude, it.longitude).toOptional() }
		.distinctUntilChanged()
		.throttleLatest(throttleDuration.toMillis(), TimeUnit.MILLISECONDS)
		.doOnNext { Timber.i("Streamed location: %s", it) }
		.replayingShare(Absent)
}
