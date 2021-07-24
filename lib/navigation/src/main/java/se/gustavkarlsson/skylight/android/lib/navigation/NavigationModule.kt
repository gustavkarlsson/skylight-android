package se.gustavkarlsson.skylight.android.lib.navigation

import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.core.AppScope

@Module
class NavigationModule(private val screens: Screens) {

    @Provides
    internal fun provideScreens(): Screens = screens

    @AppScope
    @Provides
    internal fun provideDefaultNavigator(
        overrides: Set<@JvmSuppressWildcards NavigationOverride>,
    ): DefaultNavigator = DefaultNavigator(overrides)

    @Provides
    internal fun provideNavigator(navigator: DefaultNavigator): Navigator = navigator

    @Provides
    internal fun provideBackPressHandler(navigator: DefaultNavigator): BackPressHandler = navigator
}
