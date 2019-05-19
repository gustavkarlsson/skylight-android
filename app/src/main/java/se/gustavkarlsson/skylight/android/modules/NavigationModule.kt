package se.gustavkarlsson.skylight.android.modules

import android.app.Activity
import androidx.navigation.findNavController
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.navigation.Navigator

val navigationModule = module {

	scope("activity") {
		get<Activity>().findNavController(R.id.mainNavHost)
	}

	scope("activity") {
		Navigator(navController = get())
	}

}
