package se.gustavkarlsson.skylight.android.services_impl.streamables

import com.hadisatrio.optional.Optional
import io.reactivex.Flowable
import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.GeomagLocation
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.extensions.delay
import se.gustavkarlsson.skylight.android.extensions.seconds
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.GeomagLocationProvider
import timber.log.Timber

class GeomagLocationProviderStreamable(
	locations: Flowable<Location>,
	geomagLocationProvider: GeomagLocationProvider
) : Streamable<GeomagLocation> {
	override val stream: Flowable<GeomagLocation> = locations
		.switchMap {
			geomagLocationProvider.get(Single.just(Optional.of(it)))
				.retryWhen { it.delay(RETRY_DELAY) }
				.toFlowable()
		}
		.doOnNext { Timber.i("Streamed geomag location: %s", it) }

	companion object {
		val RETRY_DELAY = 5.seconds
	}
}
