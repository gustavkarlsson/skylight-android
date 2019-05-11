package se.gustavkarlsson.skylight.android.modules

import io.reactivex.Flowable
import io.reactivex.Single
import org.koin.dsl.module.module
import se.gustavkarlsson.koptional.Optional
import se.gustavkarlsson.koptional.optionalOf
import se.gustavkarlsson.skylight.android.entities.Location
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

}

class TestLocationProvider(
	var delegate: () -> Optional<Location>
) : LocationProvider {
	override fun get(): Single<Optional<Location>> {
		return Single.fromCallable { delegate() }
	}

	override fun stream(): Flowable<Optional<Location>> {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}
}
