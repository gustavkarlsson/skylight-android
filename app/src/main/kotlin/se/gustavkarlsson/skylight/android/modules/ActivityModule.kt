package se.gustavkarlsson.skylight.android.modules

import android.app.Activity
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.gui.MainActivity

val activityModule = module {

	scope("activity") { (activity: Activity) ->
		activity
	}

	single<Class<out Activity>>("activity") {
		MainActivity::class.java
	}

}
