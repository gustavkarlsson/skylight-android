package se.gustavkarlsson.skylight.android.lib.navigationsetup

import dagger.Module
import dagger.Provides
import dagger.Reusable

@Module
internal object NavigationSetupModule {

    @Provides
    @Reusable
    internal fun provideNavigationInstaller(): NavigationInstaller = ViewModelNavigationInstaller
}
