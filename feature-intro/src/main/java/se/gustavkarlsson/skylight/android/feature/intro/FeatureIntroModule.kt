package se.gustavkarlsson.skylight.android.feature.intro

import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.multibindings.IntoSet
import se.gustavkarlsson.skylight.android.lib.navigation.Backstack
import se.gustavkarlsson.skylight.android.lib.navigation.NavigationOverride
import se.gustavkarlsson.skylight.android.lib.runversion.RunVersionManager

@Module
class FeatureIntroModule {

    @Provides
    @Reusable
    @IntoSet
    internal fun navigationOverride(runVersionManager: RunVersionManager): NavigationOverride =
        object : NavigationOverride {
            override val priority = 10

            override fun override(oldBackstack: Backstack, targetBackstack: Backstack) =
                when {
                    oldBackstack.isNotEmpty() && targetBackstack.isEmpty() -> null
                    runVersionManager.isFirstRun -> listOf(IntroScreen(targetBackstack))
                    else -> null
                }
        }
}
