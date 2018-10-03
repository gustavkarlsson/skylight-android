package se.gustavkarlsson.skylight.android.locationname

import io.reactivex.Flowable
import io.reactivex.Single
import org.threeten.bp.Duration
import se.gustavkarlsson.koptional.Optional
import se.gustavkarlsson.koptional.optionalOf
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.extensions.delay
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.LocationNameProvider
import timber.log.Timber

internal class LocationNameProviderStreamable(
	locations: Flowable<Location>,
	locationNameProvider: LocationNameProvider,
	retryDelay: Duration
) : Streamable<Optional<String>> {
	override val stream: Flowable<Optional<String>> = locations
		.switchMap {
			locationNameProvider.get(Single.just(optionalOf(it)))
				.retryWhen { it.delay(retryDelay) }
				.toFlowable()
		}
		.distinctUntilChanged()
		.doOnNext { Timber.i("Streamed location name: %s", it.value) }
}
