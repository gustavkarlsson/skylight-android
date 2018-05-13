package se.gustavkarlsson.skylight.android.services_impl.streamables

import com.hadisatrio.optional.Optional
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.entities.Darkness
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.extensions.delay
import se.gustavkarlsson.skylight.android.extensions.minutes
import se.gustavkarlsson.skylight.android.extensions.seconds
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.DarknessProvider
import timber.log.Timber

class DarknessProviderStreamable(
	locations: Flowable<Location>,
	darknessProvider: DarknessProvider,
	now: Single<Instant>
) : Streamable<Darkness> {

	private val timeUpdates: Flowable<Instant> = now
		.repeatWhen { it.delay(POLLING_INTERVAL) }

	override val stream: Flowable<Darkness> =
		Flowable.combineLatest(locations, timeUpdates,
			BiFunction<Location, Instant, Single<Darkness>> { location, time ->
				darknessProvider.get(Single.just(time), Single.just(Optional.of(location)))
					.retryWhen { it.delay(RETRY_DELAY) }
			})
			.switchMap {
				it.toFlowable()
			}
			.doOnNext { Timber.i("Streamed darkness: %s", it) }

	companion object {
		val POLLING_INTERVAL = 1.minutes
		val RETRY_DELAY = 5.seconds
	}
}
