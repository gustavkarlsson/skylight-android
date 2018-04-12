package se.gustavkarlsson.skylight.android.di.modules

import com.f2prateek.rx.preferences2.RxSharedPreferences

class RealRxSharedPreferencesModule(
	sharedPreferencesModule: SharedPreferencesModule
) : RxSharedPreferencesModule {

	override val rxSharedPreferences: RxSharedPreferences by lazy {
		RxSharedPreferences.create(sharedPreferencesModule.sharedPreferences)
	}
}
