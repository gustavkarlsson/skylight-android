package se.gustavkarlsson.skylight.android.modules

import android.content.Context
import org.koin.dsl.module.module

val testSharedPreferencesModule = module {

	single(override = true) {
		val context = get<Context>()
		val name = context.packageName + "_preferences_test"
		context.getSharedPreferences(name, Context.MODE_PRIVATE)
	}
}
