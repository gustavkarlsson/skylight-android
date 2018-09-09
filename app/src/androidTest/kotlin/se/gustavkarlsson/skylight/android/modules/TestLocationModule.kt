package se.gustavkarlsson.skylight.android.modules

import com.hadisatrio.optional.Optional
import io.reactivex.Flowable
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.extensions.delay
import se.gustavkarlsson.skylight.android.extensions.minutes
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.LocationProvider
import se.gustavkarlsson.skylight.android.test.TestLocationProvider

val testLocationModule = module {

	single {
		TestLocationProvider { Optional.of(Location(0.0, 0.0)) }
	}

	single<LocationProvider>(override = true) {
		get<TestLocationProvider>()
	}

	single<Streamable<Location>>("location", override = true) {
		val locationProvider = get<LocationProvider>()
		object : Streamable<Location> {
			override val stream: Flowable<Location>
				get() = locationProvider.get()
					.repeatWhen { it.delay(15.minutes) }
					.filter(Optional<Location>::isPresent)
					.map(Optional<Location>::get)
		}
	}

}