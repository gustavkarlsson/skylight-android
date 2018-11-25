package se.gustavkarlsson.skylight.android.modules

import androidx.navigation.NavController
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.navigation.Navigator

val navigationModule = module {

	scope("activity") { (navController: NavController) ->
		navController
	}

	scope("activity") {
		Navigator(get())
	}

}
