package se.gustavkarlsson.skylight.android.lib.navigationsetup

import dagger.Module
import dagger.Provides
import dagger.Reusable

@Module
class LibNavigationSetupModule {

    @Provides
    @Reusable
    internal fun provideNavigationInstaller(): NavigationInstaller =
        SimpleStackNavigationInstaller(
            DefaultDirectionsCalculator
        )
}
