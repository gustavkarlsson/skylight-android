package se.gustavkarlsson.skylight.android.services_impl.streamables

import android.annotation.SuppressLint
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.Duration
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.extensions.delay
import se.gustavkarlsson.skylight.android.services.Streamable
import timber.log.Timber

class ReactiveLocationProviderStreamable(
	reactiveLocationProvider: ReactiveLocationProvider,
	firstUpdatePollingInterval: Duration,
	pollingInterval: Duration,
	retryDelay: Duration
) : Streamable<Location> {

	private val firstLocationRequest = LocationRequest().apply {
		priority = PRIORITY_HIGH_ACCURACY
		interval = firstUpdatePollingInterval.toMillis()
		numUpdates = 1
	}

	private val locationRequest = LocationRequest().apply {
		priority = PRIORITY_HIGH_ACCURACY
		interval = pollingInterval.toMillis()
	}

	@SuppressLint("MissingPermission")
	override val stream: Flowable<Location> = reactiveLocationProvider
		.getUpdatedLocation(firstLocationRequest)
		.firstOrError()
		.concatWith {
			reactiveLocationProvider.getUpdatedLocation(locationRequest)
		}
		.subscribeOn(Schedulers.io())
		.doOnError { Timber.e(it) }
		.retryWhen { it.delay(retryDelay) }
		.map { Location(it.latitude, it.longitude) }
		.doOnNext { Timber.i("Streamed location: %s", it) }
}
