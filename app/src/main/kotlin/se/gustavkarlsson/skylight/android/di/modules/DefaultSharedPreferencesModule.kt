package se.gustavkarlsson.skylight.android.di.modules

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

class DefaultSharedPreferencesModule(context: Context) : SharedPreferencesModule {
	override val sharedPreferences: SharedPreferences by lazy {
		PreferenceManager.getDefaultSharedPreferences(context)
	}
}
