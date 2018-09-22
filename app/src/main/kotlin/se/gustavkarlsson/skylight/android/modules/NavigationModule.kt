package se.gustavkarlsson.skylight.android.modules

import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.navigation.Navigator

val navigationModule = module {

	single {
		Navigator()
	}

}
