package se.gustavkarlsson.skylight.android.di.modules

import com.hadisatrio.optional.Optional
import io.reactivex.Flowable
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.extensions.minutes
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.LocationProvider
import se.gustavkarlsson.skylight.android.test.TestLocationProvider
import java.util.concurrent.TimeUnit

class TestLocationModule(testLocationProvider: TestLocationProvider) : LocationModule {

	override val locationProvider: LocationProvider = testLocationProvider

	override val locationStreamable: Streamable<Location> by lazy {
		object : Streamable<Location> {
			override val stream: Flowable<Location>
				get() = locationProvider.get()
					.repeatWhen { it.delay(POLLING_INTERVAL.toMillis(), TimeUnit.MILLISECONDS) }
					.filter(Optional<Location>::isPresent)
					.map(Optional<Location>::get)
		}
	}

	override val locationFlowable: Flowable<Location> by lazy {
		locationStreamable.stream
			.replay(1)
			.refCount()
	}

	companion object {
		private val POLLING_INTERVAL = 15.minutes
	}
}
