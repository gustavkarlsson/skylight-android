package se.gustavkarlsson.skylight.android.services_impl.providers

import com.hadisatrio.optional.Optional
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.services.providers.LocationProvider
import timber.log.Timber
import java.util.concurrent.TimeUnit

class ReactiveLocationLocationProvider(
	private val reactiveLocationProvider: ReactiveLocationProvider
) : LocationProvider {

	override fun get(): Single<Optional<Location>> {
		return reactiveLocationProvider.lastKnownLocation
			.subscribeOn(Schedulers.io())
			.singleOrError()
			.timeout(timeoutMillis, TimeUnit.MILLISECONDS)
			.map { Optional.of(Location(it.latitude, it.longitude)) }
			.doOnError { Timber.w(it, "Failed to get location") }
			.onErrorReturnItem(Optional.absent())
			.doOnSuccess { Timber.i("Provided location: %s", it) }
	}

	companion object {
		// TODO Make configurable
		private const val timeoutMillis = 5000L
	}
}
