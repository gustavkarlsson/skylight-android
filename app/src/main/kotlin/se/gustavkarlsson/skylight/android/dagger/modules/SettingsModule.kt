package se.gustavkarlsson.skylight.android.dagger.modules

import dagger.Binds
import dagger.Module
import dagger.Reusable
import se.gustavkarlsson.skylight.android.services.Settings
import se.gustavkarlsson.skylight.android.services_impl.SharedPreferencesSettings

@Module(includes = arrayOf(
        ContextModule::class
))
abstract class SettingsModule {

    @Binds
    @Reusable
    abstract fun bindSettings(impl: SharedPreferencesSettings): Settings
}
