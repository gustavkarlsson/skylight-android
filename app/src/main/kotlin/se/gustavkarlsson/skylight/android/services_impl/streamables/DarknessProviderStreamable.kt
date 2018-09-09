package se.gustavkarlsson.skylight.android.services_impl.streamables

import com.hadisatrio.optional.Optional
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.entities.Darkness
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.extensions.delay
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.DarknessProvider
import se.gustavkarlsson.skylight.android.services.providers.TimeProvider
import timber.log.Timber

class DarknessProviderStreamable(
	locations: Flowable<Location>,
	darknessProvider: DarknessProvider,
	timeProvider: TimeProvider,
	pollingInterval: Duration,
	retryDelay: Duration
) : Streamable<Darkness> {

	private val timeUpdates: Flowable<Instant> = timeProvider.getTime()
		.repeatWhen { it.delay(pollingInterval) }

	override val stream: Flowable<Darkness> =
		Flowable.combineLatest(locations, timeUpdates,
			BiFunction<Location, Instant, Single<Darkness>> { location, time ->
				darknessProvider.get(Single.just(time), Single.just(Optional.of(location)))
					.retryWhen { it.delay(retryDelay) }
			})
			.switchMap {
				it.toFlowable()
			}
			.doOnNext { Timber.i("Streamed darkness: %s", it) }
}
