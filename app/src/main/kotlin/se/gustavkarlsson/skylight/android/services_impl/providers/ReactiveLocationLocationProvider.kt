package se.gustavkarlsson.skylight.android.services_impl.providers

import android.annotation.SuppressLint
import com.google.android.gms.location.LocationRequest
import com.hadisatrio.optional.Optional
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.Duration
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.extensions.seconds
import se.gustavkarlsson.skylight.android.services.providers.LocationProvider
import timber.log.Timber

class ReactiveLocationLocationProvider(
	private val reactiveLocationProvider: ReactiveLocationProvider,
	private val timeout: Duration = 5.seconds
) : LocationProvider {
	private val locationRequest = LocationRequest().apply {
		priority = LocationRequest.PRIORITY_LOW_POWER
		numUpdates = 1
		maxWaitTime = timeout.toMillis()
	}

	@SuppressLint("MissingPermission")
	override fun get(): Single<Optional<Location>> {
		return reactiveLocationProvider.getUpdatedLocation(locationRequest)
			.subscribeOn(Schedulers.io())
			.singleOrError()
			.map { Optional.of(Location(it.latitude, it.longitude)) }
			.doOnError { Timber.w(it, "Failed to get location") }
			.onErrorReturnItem(Optional.absent())
			.doOnSuccess { Timber.i("Provided location: %s", it) }
	}
}
