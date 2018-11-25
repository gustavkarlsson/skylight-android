package se.gustavkarlsson.skylight.android.modules

import androidx.navigation.NavController
import androidx.navigation.ui.AppBarConfiguration
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.navigation.Navigator

val navigationModule = module {

	scope("activity") { (navController: NavController) ->
		navController
	}

	scope("activity") {
		Navigator(get())
	}

	single {
		AppBarConfiguration(
			setOf(
				// TODO Derive from Screen
				R.id.mainFragment,
				R.id.introFragment,
				R.id.googlePlayServicesFragment,
				R.id.permissionFragment
			)
		)
	}

}
