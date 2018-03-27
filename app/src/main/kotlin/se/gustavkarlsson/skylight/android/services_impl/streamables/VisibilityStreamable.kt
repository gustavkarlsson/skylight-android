package se.gustavkarlsson.skylight.android.services_impl.streamables

import io.reactivex.Flowable
import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.entities.Visibility
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.VisibilityProvider

class VisibilityStreamable(
	locations: Flowable<Location>,
	visibilityProvider: VisibilityProvider
) : Streamable<Visibility> {
	override val stream: Flowable<Visibility> = locations
		.switchMap {
			visibilityProvider.get(Single.just(it))
				.toFlowable()
		}
		.replay(1)
		.refCount()
}
