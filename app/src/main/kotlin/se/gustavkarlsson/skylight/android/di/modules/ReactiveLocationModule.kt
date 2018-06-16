package se.gustavkarlsson.skylight.android.di.modules

import android.Manifest
import io.reactivex.Flowable
import pl.charmas.android.reactivelocation2.ReactiveLocationProvider
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.LocationProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.ReactiveLocationLocationProvider
import se.gustavkarlsson.skylight.android.services_impl.streamables.ReactiveLocationProviderStreamable

class ReactiveLocationModule(contextModule: ContextModule) : LocationModule {

	private val reactiveLocationProvider: ReactiveLocationProvider by lazy {
		ReactiveLocationProvider(contextModule.context)
	}

	override val locationProvider: LocationProvider by lazy {
		ReactiveLocationLocationProvider(reactiveLocationProvider)
	}
	private val locationStreamable: Streamable<Location> by lazy {
		ReactiveLocationProviderStreamable(reactiveLocationProvider)
	}

	override val locationFlowable: Flowable<Location> by lazy {
		locationStreamable.stream
			.replay(1)
			.refCount()
	}

	override val locationPermission = Manifest.permission.ACCESS_COARSE_LOCATION
}
