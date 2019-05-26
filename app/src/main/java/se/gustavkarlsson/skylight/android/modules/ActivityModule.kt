package se.gustavkarlsson.skylight.android.modules

import android.app.Activity
import androidx.fragment.app.FragmentManager
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.gui.MainActivity

val activityModule = module {

	scope("activity") { (activity: Activity) ->
		activity
	}

	scope("activity") { (fragmentManager: FragmentManager) ->
		fragmentManager
	}

	single<Class<out Activity>>("activity") {
		MainActivity::class.java
	}

}
