package se.gustavkarlsson.skylight.android.services_impl.streamables

import io.reactivex.Flowable
import io.reactivex.Single
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.entities.GeomagLocation
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.GeomagLocationProvider
import timber.log.Timber
import java.util.concurrent.TimeUnit

class GeomagLocationProviderStreamable(
	locations: Flowable<Location>,
	geomagLocationProvider: GeomagLocationProvider,
	retryDelay: Duration
) : Streamable<GeomagLocation> {
	override val stream: Flowable<GeomagLocation> = locations
		.switchMap {
			geomagLocationProvider.get(Single.just(it))
				.retryWhen { it.delay(retryDelay.toMillis(), TimeUnit.MILLISECONDS) }
				.toFlowable()
		}
		.doOnNext { Timber.i("Streamed geomag location: %s", it) }
		.replay(1)
		.refCount()
}
