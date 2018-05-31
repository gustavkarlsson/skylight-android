package se.gustavkarlsson.skylight.android.services_impl.streamables

import com.hadisatrio.optional.Optional
import io.reactivex.Flowable
import io.reactivex.Single
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.entities.GeomagLocation
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.extensions.delay
import se.gustavkarlsson.skylight.android.extensions.seconds
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.GeomagLocationProvider
import timber.log.Timber

class GeomagLocationProviderStreamable(
	locations: Flowable<Location>,
	geomagLocationProvider: GeomagLocationProvider,
	retryDelay: Duration = 5.seconds
) : Streamable<GeomagLocation> {
	override val stream: Flowable<GeomagLocation> = locations
		.switchMap {
			geomagLocationProvider.get(Single.just(Optional.of(it)))
				.retryWhen { it.delay(retryDelay) }
				.toFlowable()
		}
		.doOnNext { Timber.i("Streamed geomag location: %s", it) }
}
