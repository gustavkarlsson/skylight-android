package se.gustavkarlsson.skylight.android.di.modules

import android.Manifest
import com.hadisatrio.optional.Optional
import io.reactivex.Flowable
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.extensions.delay
import se.gustavkarlsson.skylight.android.extensions.minutes
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.LocationProvider
import se.gustavkarlsson.skylight.android.test.TestLocationProvider

class TestLocationModule(testLocationProvider: TestLocationProvider) : LocationModule {

	override val locationProvider: LocationProvider = testLocationProvider

	private val locationStreamable: Streamable<Location> by lazy {
		object : Streamable<Location> {
			override val stream: Flowable<Location>
				get() = locationProvider.get()
					.repeatWhen { it.delay(POLLING_INTERVAL) }
					.filter(Optional<Location>::isPresent)
					.map(Optional<Location>::get)
		}
	}

	override val locationFlowable: Flowable<Location> by lazy {
		locationStreamable.stream
			.replay(1)
			.refCount()
	}

	override val locationPermission = Manifest.permission.ACCESS_COARSE_LOCATION

	companion object {
		val POLLING_INTERVAL = 15.minutes
	}
}
