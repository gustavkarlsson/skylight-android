package se.gustavkarlsson.skylight.android.modules

import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.services.RunVersionManager
import se.gustavkarlsson.skylight.android.test.TestRunVersionManager

val testRunVersionsModule = module {

	single {
		TestRunVersionManager { false }
	}

	single<RunVersionManager>(override = true) {
		get<TestRunVersionManager>()
	}

}
