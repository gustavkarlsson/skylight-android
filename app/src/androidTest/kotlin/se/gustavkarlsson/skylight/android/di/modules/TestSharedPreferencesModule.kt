package se.gustavkarlsson.skylight.android.di.modules

import android.content.Context
import android.content.SharedPreferences
import se.gustavkarlsson.skylight.android.test.getSharedPreferencesName

class TestSharedPreferencesModule(context: Context) : SharedPreferencesModule {

	override val sharedPreferences: SharedPreferences by lazy {
		context.getSharedPreferences(getSharedPreferencesName(), Context.MODE_PRIVATE)
	}
}
