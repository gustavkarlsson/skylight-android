package se.gustavkarlsson.skylight.android.di.modules

import io.reactivex.Flowable
import se.gustavkarlsson.skylight.android.entities.GeomagLocation
import se.gustavkarlsson.skylight.android.extensions.seconds
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.GeomagLocationProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.GeomagLocationProviderImpl
import se.gustavkarlsson.skylight.android.services_impl.streamables.GeomagLocationProviderStreamable

class RealGeomagLocationModule(locationModule: LocationModule) : GeomagLocationModule {

	override val geomagLocationProvider: GeomagLocationProvider by lazy {
		GeomagLocationProviderImpl()
	}

	override val geomagLocationStreamable: Streamable<GeomagLocation> by lazy {
		GeomagLocationProviderStreamable(
			locationModule.locationFlowable,
			geomagLocationProvider,
			RETRY_DELAY
		)
	}

	override val geomagLocationFlowable: Flowable<GeomagLocation> by lazy {
		geomagLocationStreamable.stream
			.replay(1)
			.refCount()
	}

	// TODO Make configurable in constructor
	companion object {
		private val RETRY_DELAY = 5.seconds
	}

}
