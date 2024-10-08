package se.gustavkarlsson.skylight.android.lib.navigation

import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.core.AppScopeMarker

@Module
@ContributesTo(AppScopeMarker::class)
class NavigationModule(private val screens: Screens) {

    @Provides
    internal fun provideScreens(): Screens = screens

    @Provides
    internal fun provideDefaultScreen(screens: Screens): Screen = screens.main

    @Provides
    internal fun provideNavigator(navigator: DefaultNavigator): Navigator = navigator
}
