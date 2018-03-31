package se.gustavkarlsson.skylight.android.services_impl.streamables

import com.hadisatrio.optional.Optional
import io.reactivex.Flowable
import io.reactivex.Single
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.LocationNameProvider
import timber.log.Timber
import java.util.concurrent.TimeUnit

class LocationNameProviderStreamable(
	locations: Flowable<Location>,
	locationNameProvider: LocationNameProvider,
	retryDelay: Duration
) : Streamable<Optional<String>> {
	override val stream: Flowable<Optional<String>> = locations
		.switchMap {
			locationNameProvider.get(Single.just(it))
				.retryWhen { it.delay(retryDelay.toMillis(), TimeUnit.MILLISECONDS) }
				.toFlowable()
		}
		.doOnNext { Timber.i("Streamed location name: %s", it.orNull()) }
}
