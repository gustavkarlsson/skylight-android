package se.gustavkarlsson.skylight.android.services_impl.streamables

import android.annotation.SuppressLint
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.Duration
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.extensions.delay
import se.gustavkarlsson.skylight.android.extensions.minutes
import se.gustavkarlsson.skylight.android.services.Streamable
import timber.log.Timber

class ReactiveLocationProviderStreamable(
	reactiveLocationProvider: ReactiveLocationProvider,
	pollingInterval: Duration = 15.minutes,
	retryDelay: Duration = 1.minutes
) : Streamable<Location> {
	private val locationRequest = LocationRequest().apply {
		priority = PRIORITY_BALANCED_POWER_ACCURACY
		interval = pollingInterval.toMillis()
	}

	@SuppressLint("MissingPermission")
	override val stream: Flowable<Location> = reactiveLocationProvider
		.getUpdatedLocation(locationRequest)
		.subscribeOn(Schedulers.io())
		.retryWhen { it.delay(retryDelay) }
		.map { Location(it.latitude, it.longitude) }
		.toFlowable(BackpressureStrategy.LATEST)
		.doOnNext { Timber.i("Streamed location: %s", it) }
}
