package se.gustavkarlsson.skylight.android.lib.navigationsetup

import dagger.Module
import dagger.Provides
import dagger.Reusable

@Module
object LibNavigationSetupModule {

    @Provides
    @Reusable
    internal fun provideNavigationInstaller(): NavigationInstaller = ViewModelNavigationInstaller
}
