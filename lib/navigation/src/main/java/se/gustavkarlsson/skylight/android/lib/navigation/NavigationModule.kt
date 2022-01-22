package se.gustavkarlsson.skylight.android.lib.navigation

import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.core.AppScope
import se.gustavkarlsson.skylight.android.lib.analytics.Analytics

@Module
class NavigationModule(private val screens: Screens) {

    @Provides
    internal fun provideScreens(): Screens = screens

    @Provides
    internal fun provideDefaultScreen(screens: Screens): Screen = screens.main

    // FIXME clean up
    @AppScope
    @Provides
    internal fun provideDefaultNavigator(
        screen: Screen,
        overrides: Set<@JvmSuppressWildcards NavigationOverride>,
        analytics: Analytics,
    ): DefaultNavigator = DefaultNavigator(defaultScreen = screen, overrides, analytics).also {
        check(overrides.size == 2) // FIXME remove
    }

    @Provides
    internal fun provideNavigator(navigator: DefaultNavigator): Navigator = navigator

    @Provides
    internal fun provideBackPressHandler(navigator: DefaultNavigator): BackPressHandler = navigator
}
