package se.gustavkarlsson.skylight.android.lib.navigation

import dagger.Module
import dagger.Provides

@Module
class NavigationModule(private val screens: Screens) {

    @Provides
    internal fun provideScreens(): Screens = screens

    @Provides
    internal fun provideDefaultScreen(screens: Screens): Screen = screens.main

    @Provides
    internal fun provideNavigator(navigator: DefaultNavigator): Navigator = navigator

    @Provides
    internal fun provideBackPressHandler(navigator: DefaultNavigator): BackPressHandler = navigator
}
