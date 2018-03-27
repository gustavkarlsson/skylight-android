package se.gustavkarlsson.skylight.android.dagger.modules

import dagger.Module
import dagger.Provides
import java.util.*
import javax.inject.Singleton

@Module
class LocalizationModule {

    @Provides
    @Singleton
    fun provideLocale(): Locale = Locale.getDefault()

}
