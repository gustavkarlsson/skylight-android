package se.gustavkarlsson.skylight.android.services_impl.streamables

import com.hadisatrio.optional.Optional
import dagger.Reusable
import io.reactivex.Flowable
import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.LocationNameProvider
import timber.log.Timber
import javax.inject.Inject

@Reusable
class LocationNameProviderStreamable
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
		.doOnNext { Timber.i("Streamed location name: %s", it.orNull()) }
		.replay(1)
		.refCount()
}
