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
import se.gustavkarlsson.skylight.android.services.Streamable
import java.util.concurrent.TimeUnit

class LocationStreamable(
	reactiveLocationProvider: ReactiveLocationProvider,
	pollingInterval: Duration,
	retryDelay: Duration
) : Streamable<Location> {
	private val locationRequest = LocationRequest().apply {
		priority = PRIORITY_BALANCED_POWER_ACCURACY
		interval = pollingInterval.toMillis()
	}

	@SuppressLint("MissingPermission")
	override val stream: Flowable<Location> = reactiveLocationProvider
		.getUpdatedLocation(locationRequest)
		.subscribeOn(Schedulers.io())
		.retryWhen { it.delay(retryDelay.toMillis(), TimeUnit.MILLISECONDS) }
		.map { Location(it.latitude, it.longitude) }
		.toFlowable(BackpressureStrategy.LATEST)
		.replay(1)
		.refCount()
}
