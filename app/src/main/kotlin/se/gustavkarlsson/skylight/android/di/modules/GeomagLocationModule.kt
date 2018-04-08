package se.gustavkarlsson.skylight.android.di.modules

import io.reactivex.Flowable
import se.gustavkarlsson.skylight.android.entities.GeomagLocation
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.GeomagLocationProvider

interface GeomagLocationModule {
	val geomagLocationProvider: GeomagLocationProvider
	val geomagLocationStreamable: Streamable<GeomagLocation>
	val geomagLocationFlowable: Flowable<GeomagLocation>
}
