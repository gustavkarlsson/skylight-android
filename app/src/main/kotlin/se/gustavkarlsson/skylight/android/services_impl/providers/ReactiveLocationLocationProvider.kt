package se.gustavkarlsson.skylight.android.services_impl.providers

import android.annotation.SuppressLint
import com.google.android.gms.location.LocationRequest
import com.hadisatrio.optional.Optional
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.threeten.bp.Duration
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.services.providers.LocationProvider
import timber.log.Timber
import java.util.concurrent.TimeUnit

class ReactiveLocationLocationProvider(
	private val reactiveLocationProvider: ReactiveLocationProvider,
	private val timeout: Duration
) : LocationProvider {

	private val locationRequest = LocationRequest().apply {
		priority = LocationRequest.PRIORITY_HIGH_ACCURACY
		interval = timeout.toMillis() / 2
		numUpdates = 1
	}

	@SuppressLint("MissingPermission")
	override fun get(): Single<Optional<Location>> {
		return reactiveLocationProvider
			.getUpdatedLocation(locationRequest)
			.subscribeOn(Schedulers.io())
			.firstOrError()
			.map { Optional.of(Location(it.latitude, it.longitude)) }
			.timeout(timeout.toMillis(), TimeUnit.MILLISECONDS)
			.doOnError { Timber.w(it, "Failed to get location") }
			.onErrorReturnItem(Optional.absent())
			.doOnSuccess { Timber.i("Provided location: %s", it) }
	}
}
