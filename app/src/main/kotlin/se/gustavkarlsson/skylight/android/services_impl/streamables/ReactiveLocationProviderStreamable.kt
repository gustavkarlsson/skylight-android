package se.gustavkarlsson.skylight.android.services_impl.streamables

import android.annotation.SuppressLint
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.Duration
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.extensions.delay
import se.gustavkarlsson.skylight.android.services.Streamable
import timber.log.Timber
import java.util.concurrent.TimeUnit

class ReactiveLocationProviderStreamable(
	reactiveLocationProvider: ReactiveLocationProvider,
	throttleDuration: Duration,
	firstPollingInterval: Duration,
	restPollingInterval: Duration,
	retryDelay: Duration
) : Streamable<Location> {

	private val firstLocationRequest = LocationRequest().apply {
		priority = PRIORITY_HIGH_ACCURACY
		interval = firstPollingInterval.toMillis()
		numUpdates = 1
	}

	private val locationRequest = LocationRequest().apply {
		priority = PRIORITY_HIGH_ACCURACY
		interval = restPollingInterval.toMillis()
	}

	@SuppressLint("MissingPermission")
	private val lastLocation = reactiveLocationProvider
		.lastKnownLocation
		.singleElement()
		.doOnSuccess { Timber.d("Last location: $it") }

	@SuppressLint("MissingPermission")
	private val forcedLocation = reactiveLocationProvider
		.getUpdatedLocation(firstLocationRequest)
		.firstOrError()
		.doOnSuccess { Timber.d("Forced location: $it") }

	@SuppressLint("MissingPermission")
	private val pollingLocations = reactiveLocationProvider
		.getUpdatedLocation(locationRequest)
		.toFlowable(BackpressureStrategy.LATEST)

	private val locations = lastLocation
		.switchIfEmpty(forcedLocation)
		.toFlowable()
		.concatWith(pollingLocations)

	@SuppressLint("MissingPermission")
	override val stream: Flowable<Location> = locations
		.distinctUntilChanged()
		.subscribeOn(Schedulers.io())
		.doOnError { Timber.e(it) }
		.retryWhen { it.delay(retryDelay) }
		.map { Location(it.latitude, it.longitude) }
		.distinctUntilChanged()
		.throttleLatest(throttleDuration.toMillis(), TimeUnit.MILLISECONDS)
		.doOnNext { Timber.i("Streamed location: %s", it) }
}
