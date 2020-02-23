package se.gustavkarlsson.skylight.android.lib.navigationsetup

import dagger.Module
import dagger.Provides
import dagger.Reusable
import se.gustavkarlsson.skylight.android.lib.navigationsetup.internal.DefaultDirectionsCalculator
import se.gustavkarlsson.skylight.android.lib.navigationsetup.internal.SimpleStackNavigationInstaller
import se.gustavkarlsson.skylight.android.services.NavigationInstaller

@Module
class LibNavigationSetupModule {

    @Provides
    @Reusable
    internal fun provideNavigationInstaller(): NavigationInstaller =
        SimpleStackNavigationInstaller(DefaultDirectionsCalculator)
}
