package se.gustavkarlsson.skylight.android.lib.runversion

import dagger.Module
import dagger.Provides

@Module
object LibRunVersionModule {

    @Provides
    internal fun runVersionManager(impl: SharedPreferencesRunVersionManager): RunVersionManager = impl
}
