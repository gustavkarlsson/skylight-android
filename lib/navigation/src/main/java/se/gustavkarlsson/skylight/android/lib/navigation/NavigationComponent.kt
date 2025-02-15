package se.gustavkarlsson.skylight.android.lib.navigation

import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import me.tatarka.inject.annotations.Scope
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.annotation.AnnotationTarget.PROPERTY_GETTER

@Component
@NavigationScope
abstract class NavigationComponent internal constructor(
    @get:Provides val screens: Screens,
    @get:Provides val navigationOverrides: Set<NavigationOverride>,
) {

    abstract val navigator: Navigator

    @Provides
    internal fun provideNavigator(impl: DefaultNavigator): Navigator = impl

    @Provides
    internal fun provideDefaultScreen(screens: Screens): Screen = screens.main

    companion object {
        lateinit var instance: NavigationComponent // FIXME how to set up instance? What about scope?
    }
}

@Scope
@Target(CLASS, FUNCTION, PROPERTY_GETTER)
annotation class NavigationScope
