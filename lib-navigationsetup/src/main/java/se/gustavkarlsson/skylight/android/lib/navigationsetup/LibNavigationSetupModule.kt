package se.gustavkarlsson.skylight.android.lib.navigationsetup

import dagger.Module
import dagger.Provides
import dagger.Reusable
import org.koin.dsl.module.module
import se.gustavkarlsson.skylight.android.lib.navigationsetup.internal.DefaultDirectionsCalculator
import se.gustavkarlsson.skylight.android.lib.navigationsetup.internal.SimpleStackNavigationInstaller

val libNavigationSetupModule = module {

    single<NavigationInstaller> {
        SimpleStackNavigationInstaller(DefaultDirectionsCalculator)
    }
}

@Module
class LibNavigationSetupModule {

    @Provides
    @Reusable
    internal fun provideNavigationInstaller(): NavigationInstaller =
        SimpleStackNavigationInstaller(DefaultDirectionsCalculator)
}
