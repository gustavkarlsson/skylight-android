package se.gustavkarlsson.skylight.android.di.modules

import android.content.SharedPreferences
import android.preference.PreferenceManager

class DefaultSharedPreferencesModule(contextModule: ContextModule) : SharedPreferencesModule {
	override val sharedPreferences: SharedPreferences by lazy {
		PreferenceManager.getDefaultSharedPreferences(contextModule.context)
	}
}
