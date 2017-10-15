package se.gustavkarlsson.skylight.android.dagger.modules

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import dagger.Module
import dagger.Provides
import dagger.Reusable

@Module
class SharedPreferencesModule {

    @Provides
    @Reusable
    fun provideSharedPreferences(
            context: Context
    ): SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
}
