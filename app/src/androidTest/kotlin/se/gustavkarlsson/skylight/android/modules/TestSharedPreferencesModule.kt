package se.gustavkarlsson.skylight.android.modules

import android.content.Context
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.test.getSharedPreferencesName

val testSharedPreferencesModule = module {

	single(override = true) {
		val context = get<Context>()
		context.getSharedPreferences(getSharedPreferencesName(), Context.MODE_PRIVATE)
	}
}
