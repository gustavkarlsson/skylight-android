package se.gustavkarlsson.skylight.android.services_impl.streamables

import com.hadisatrio.optional.Optional
import io.reactivex.Flowable
import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.extensions.delay
import se.gustavkarlsson.skylight.android.extensions.seconds
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.LocationNameProvider
import timber.log.Timber

class LocationNameProviderStreamable(
	locations: Flowable<Location>,
	locationNameProvider: LocationNameProvider
) : Streamable<Optional<String>> {
	override val stream: Flowable<Optional<String>> = locations
		.switchMap {
			locationNameProvider.get(Single.just(Optional.of(it)))
				.retryWhen { it.delay(RETRY_DELAY) }
				.toFlowable()
		}
		.doOnNext { Timber.i("Streamed location name: %s", it.orNull()) }

	companion object {
		val RETRY_DELAY = 10.seconds
	}
}
