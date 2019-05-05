package se.gustavkarlsson.skylight.android.modules

import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.services.GooglePlayServicesChecker

val testGooglePlayServicesModule = module {

	single {
		TestGooglePlayServicesChecker { true }
	}

	single<GooglePlayServicesChecker>(override = true) {
		get<TestGooglePlayServicesChecker>()
	}

}

class TestGooglePlayServicesChecker(
	var delegateIsAvailable: () -> Boolean
) : GooglePlayServicesChecker {

	override val isAvailable: Boolean
		get() = delegateIsAvailable()

}
