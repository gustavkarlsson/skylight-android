package se.gustavkarlsson.skylight.android.services_impl.streamables

import com.hadisatrio.optional.Optional
import io.reactivex.Flowable
import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.GeomagLocation
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.entities.Report
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.GeomagLocationProvider
import timber.log.Timber

class GeomagLocationProviderStreamable(
	locations: Flowable<Location>,
	geomagLocationProvider: GeomagLocationProvider
) : Streamable<Report<GeomagLocation>> {
	override val stream: Flowable<Report<GeomagLocation>> = locations
		.switchMapSingle {
			geomagLocationProvider.get(Single.just(Optional.of(it)))
		}
		.distinctUntilChanged()
		.doOnNext { Timber.i("Streamed geomag location: %s", it) }
}
