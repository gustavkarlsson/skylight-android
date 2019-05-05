package se.gustavkarlsson.skylight.android.modules

import io.reactivex.Flowable
import io.reactivex.Single
import org.koin.dsl.module.module
import se.gustavkarlsson.koptional.Optional
import se.gustavkarlsson.koptional.optionalOf
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.extensions.delay
import se.gustavkarlsson.skylight.android.extensions.minutes
import se.gustavkarlsson.skylight.android.services.Streamable
import se.gustavkarlsson.skylight.android.services.providers.LocationProvider

val testLocationModule = module {

	single {
		TestLocationProvider {
			optionalOf(
				Location(
					0.0,
					0.0
				)
			)
		}
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
					.map(Optional<Location>::unsafeValue)
		}
	}

}

class TestLocationProvider(
	var delegate: () -> Optional<Location>
) : LocationProvider {
	override fun get(): Single<Optional<Location>> {
		return Single.fromCallable { delegate() }
	}
}
