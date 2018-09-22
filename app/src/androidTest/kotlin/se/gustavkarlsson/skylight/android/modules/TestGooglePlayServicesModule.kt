package se.gustavkarlsson.skylight.android.modules

import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.services.GooglePlayServicesChecker
import se.gustavkarlsson.skylight.android.test.TestGooglePlayServicesChecker

val testGooglePlayServicesModule = module {

	single {
		TestGooglePlayServicesChecker { true}
	}

	single<GooglePlayServicesChecker>(override = true) {
		get<TestGooglePlayServicesChecker>()
	}

}
