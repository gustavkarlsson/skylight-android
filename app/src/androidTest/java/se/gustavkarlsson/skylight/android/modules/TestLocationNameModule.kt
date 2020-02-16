package se.gustavkarlsson.skylight.android.modules

import io.reactivex.Observable
import io.reactivex.Single
import org.koin.dsl.module.module
import se.gustavkarlsson.koptional.Optional
import se.gustavkarlsson.koptional.optionalOf
import se.gustavkarlsson.skylight.android.entities.Location
import se.gustavkarlsson.skylight.android.services.providers.LocationNameProvider

val testLocationNameModule = module {

	single {
		TestLocationNameProvider { optionalOf("Garden of Eden") }
	}

	single<LocationNameProvider>(override = true) {
		get<TestLocationNameProvider>()
	}

}

class TestLocationNameProvider(
	var delegate: () -> Optional<String>
) : LocationNameProvider {

	override fun get(location: Single<Optional<Location>>): Single<Optional<String>> {
		return Single.fromCallable { delegate() }
	}

	override fun stream(locations: Observable<Optional<Location>>): Observable<Optional<String>> {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}
}
