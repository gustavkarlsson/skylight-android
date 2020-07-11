package se.gustavkarlsson.skylight.android.feature.intro

import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.multibindings.IntoSet
import se.gustavkarlsson.skylight.android.lib.navigation.Backstack
import se.gustavkarlsson.skylight.android.lib.navigation.NavigationOverride
import se.gustavkarlsson.skylight.android.lib.navigation.ScreenName
import se.gustavkarlsson.skylight.android.lib.runversion.RunVersionManager

@Module
object FeatureIntroModule {

    @Provides
    @Reusable
    @IntoSet
    internal fun navigationOverride(runVersionManager: RunVersionManager): NavigationOverride =
        object : NavigationOverride {
            override val priority = 10

            override fun override(oldBackstack: Backstack, targetBackstack: Backstack): List<IntroScreen>? =
                when {
                    isExiting(oldBackstack, targetBackstack) -> null
                    isGoingFromIntroToPrivacyPolicy(oldBackstack, targetBackstack) -> null
                    runVersionManager.isFirstRun -> listOf(IntroScreen(targetBackstack))
                    else -> null
                }
        }
}

private fun isExiting(oldBackstack: Backstack, targetBackstack: Backstack) =
    oldBackstack.isNotEmpty() &&
        targetBackstack.isEmpty()

private fun isGoingFromIntroToPrivacyPolicy(oldBackstack: Backstack, targetBackstack: Backstack) =
    oldBackstack.size == 1 &&
        oldBackstack[0].name == ScreenName.Intro &&
        targetBackstack.size == 2 &&
        targetBackstack[0].name == ScreenName.Intro &&
        targetBackstack[1].name == ScreenName.PrivacyPolicy
