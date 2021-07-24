package se.gustavkarlsson.skylight.android.feature.intro

import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.multibindings.IntoSet
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.runBlocking
import se.gustavkarlsson.skylight.android.core.Io
import se.gustavkarlsson.skylight.android.lib.navigation.Backstack
import se.gustavkarlsson.skylight.android.lib.navigation.NavigationOverride
import se.gustavkarlsson.skylight.android.lib.navigation.Screen
import se.gustavkarlsson.skylight.android.lib.runversion.RunVersionManager

@Module
object FeatureIntroModule {

    @Provides
    @Reusable
    @IntoSet
    internal fun navigationOverride(
        runVersionManager: RunVersionManager,
        @Io dispatcher: CoroutineDispatcher,
    ): NavigationOverride = object : NavigationOverride {
        override val priority = 10

        override fun override(oldBackstack: Backstack, targetBackstack: Backstack): Backstack? =
            runBlocking(dispatcher) {
                when {
                    targetBackstack.isNotEmpty() &&
                        targetBackstack.none { it.type == Screen.Type.Intro } &&
                        runVersionManager.isFirstRun ->
                        listOf(IntroScreen(targetBackstack))
                    else -> null
                }
            }
    }
}
