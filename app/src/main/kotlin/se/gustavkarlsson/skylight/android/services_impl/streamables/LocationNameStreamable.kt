package se.gustavkarlsson.skylight.android.services_impl.streamables

import com.hadisatrio.optional.Optional
import dagger.Reusable
import io.reactivex.Flowable
import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.LocationNameProvider
import javax.inject.Inject

@Reusable
class LocationNameStreamable
@Inject
constructor(
	locations: Flowable<Location>,
	locationNameProvider: LocationNameProvider
) : Streamable<Optional<String>> {
	override val stream: Flowable<Optional<String>> = locations
		.switchMap {
			locationNameProvider.get(Single.just(it))
				.toFlowable()
		}
		.replay(1)
		.refCount()
}
