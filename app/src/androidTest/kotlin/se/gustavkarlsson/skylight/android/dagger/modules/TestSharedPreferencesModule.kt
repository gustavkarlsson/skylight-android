package se.gustavkarlsson.skylight.android.dagger.modules

import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.Reusable
import se.gustavkarlsson.skylight.android.test.getSharedPreferencesName

@Module
class TestSharedPreferencesModule {

	@Provides
	@Reusable
	fun provideSharedPreferences(
		context: Context
	): SharedPreferences = context.getSharedPreferences(getSharedPreferencesName(), Context.MODE_PRIVATE)
}
