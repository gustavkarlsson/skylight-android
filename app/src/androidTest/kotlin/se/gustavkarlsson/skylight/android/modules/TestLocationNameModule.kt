package se.gustavkarlsson.skylight.android.modules

import com.hadisatrio.optional.Optional
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.services.providers.LocationNameProvider
import se.gustavkarlsson.skylight.android.test.TestLocationNameProvider

val testLocationNameModule = module {

	single {
		TestLocationNameProvider { Optional.of("Garden of Eden") }
	}

	single<LocationNameProvider>(override = true) {
		get<TestLocationNameProvider>()
	}

}
