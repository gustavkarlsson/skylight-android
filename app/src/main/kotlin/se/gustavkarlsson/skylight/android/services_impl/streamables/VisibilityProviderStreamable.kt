package se.gustavkarlsson.skylight.android.services_impl.streamables

import com.hadisatrio.optional.Optional
import io.reactivex.Flowable
import io.reactivex.Single
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.entities.Visibility
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.VisibilityProvider
import timber.log.Timber
import java.util.concurrent.TimeUnit

class VisibilityProviderStreamable(
	locations: Flowable<Location>,
	visibilityProvider: VisibilityProvider,
	pollingInterval: Duration,
	retryDelay: Duration
) : Streamable<Visibility> {
	override val stream: Flowable<Visibility> = locations
		.switchMap {
			visibilityProvider.get(Single.just(Optional.of(it)))
				.repeatWhen { it.delay(pollingInterval.toMillis(), TimeUnit.MILLISECONDS) }
				.retryWhen { it.delay(retryDelay.toMillis(), TimeUnit.MILLISECONDS) }
		}
		.doOnNext { Timber.i("Streamed visibility: %s", it) }
}
