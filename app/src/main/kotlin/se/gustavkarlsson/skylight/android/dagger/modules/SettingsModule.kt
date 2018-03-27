package se.gustavkarlsson.skylight.android.dagger.modules

import android.content.Context
import android.content.SharedPreferences
import com.f2prateek.rx.preferences2.RxSharedPreferences
import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.services.Settings
import se.gustavkarlsson.skylight.android.services_impl.RxPreferencesSettings
import javax.inject.Singleton


@Module
class SettingsModule {

	@Provides
	@Singleton
	fun provideRxSharedPreferences(sharedPreferences: SharedPreferences): RxSharedPreferences =
		RxSharedPreferences.create(sharedPreferences)

	@Provides
	@Singleton
	fun provideSettings(context: Context, rxSharedPreferences: RxSharedPreferences): Settings {
		return RxPreferencesSettings(
			context,
			rxSharedPreferences
		)
	}
}
