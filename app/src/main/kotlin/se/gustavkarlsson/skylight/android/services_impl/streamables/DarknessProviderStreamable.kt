package se.gustavkarlsson.skylight.android.services_impl.streamables

import com.hadisatrio.optional.Optional
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import org.threeten.bp.Duration
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.entities.Darkness
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.DarknessProvider
import timber.log.Timber
import java.util.concurrent.TimeUnit

class DarknessProviderStreamable(
	locations: Flowable<Location>,
	darknessProvider: DarknessProvider,
	now: Single<Instant>,
	pollingInterval: Duration,
	retryDelay: Duration
) : Streamable<Darkness> {

	private val timeUpdates: Flowable<Instant> = now
		.repeatWhen { it.delay(pollingInterval.toMillis(), TimeUnit.MILLISECONDS) }

	override val stream: Flowable<Darkness> =
		Flowable.combineLatest(locations, timeUpdates,
			BiFunction<Location, Instant, Single<Darkness>> { location, time ->
				darknessProvider.get(Single.just(time), Single.just(Optional.of(location)))
					.retryWhen { it.delay(retryDelay.toMillis(), TimeUnit.MILLISECONDS) }
			})
			.switchMap {
				it.toFlowable()
			}
			.doOnNext { Timber.i("Streamed darkness: %s", it) }
}
