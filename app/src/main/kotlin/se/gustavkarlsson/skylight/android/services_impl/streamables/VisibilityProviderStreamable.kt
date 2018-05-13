package se.gustavkarlsson.skylight.android.services_impl.streamables

import com.hadisatrio.optional.Optional
import io.reactivex.Flowable
import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.entities.Visibility
import se.gustavkarlsson.skylight.android.extensions.delay
import se.gustavkarlsson.skylight.android.extensions.minutes
import se.gustavkarlsson.skylight.android.extensions.seconds
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.VisibilityProvider
import timber.log.Timber

class VisibilityProviderStreamable(
	locations: Flowable<Location>,
	visibilityProvider: VisibilityProvider
) : Streamable<Visibility> {
	override val stream: Flowable<Visibility> = locations
		.switchMap {
			visibilityProvider.get(Single.just(Optional.of(it)))
				.repeatWhen { it.delay(POLLING_INTERVAL) }
				.retryWhen { it.delay(RETRY_DELAY) }
		}
		.doOnNext { Timber.i("Streamed visibility: %s", it) }

	companion object {
		val POLLING_INTERVAL = 15.minutes
		val RETRY_DELAY = 10.seconds
	}
}
