package se.gustavkarlsson.skylight.android.services_impl.streamables

import io.reactivex.Flowable
import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.entities.Visibility
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.VisibilityProvider
import timber.log.Timber

class VisibilityProviderStreamable(
	locations: Flowable<Location>,
	visibilityProvider: VisibilityProvider
) : Streamable<Visibility> {
	override val stream: Flowable<Visibility> = locations
		.switchMap {
			visibilityProvider.get(Single.just(it))
				.toFlowable()
		}
		.doOnNext { Timber.i("Streamed visibility: %s", it) }
		.replay(1)
		.refCount()
}
