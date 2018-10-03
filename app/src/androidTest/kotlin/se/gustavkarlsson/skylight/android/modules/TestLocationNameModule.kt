package se.gustavkarlsson.skylight.android.modules

import org.koin.dsl.module.module
import se.gustavkarlsson.koptional.optionalOf
import se.gustavkarlsson.skylight.android.services.providers.LocationNameProvider
import se.gustavkarlsson.skylight.android.test.TestLocationNameProvider

val testLocationNameModule = module {

	single {
		TestLocationNameProvider { optionalOf("Garden of Eden") }
	}

	single<LocationNameProvider>(override = true) {
		get<TestLocationNameProvider>()
	}

}
