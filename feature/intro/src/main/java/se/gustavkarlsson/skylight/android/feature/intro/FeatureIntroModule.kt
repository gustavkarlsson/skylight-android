package se.gustavkarlsson.skylight.android.feature.intro

import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.multibindings.IntoSet
import se.gustavkarlsson.skylight.android.lib.navigation.Backstack
import se.gustavkarlsson.skylight.android.lib.navigation.NavigationOverride
import se.gustavkarlsson.skylight.android.lib.navigation.ScreenName
import se.gustavkarlsson.skylight.android.lib.runversion.RunVersionManager
import se.gustavkarlsson.skylight.android.utils.allowDiskReadsInStrictMode

@Module
class FeatureIntroModule {

    @Provides
    @Reusable
    @IntoSet
    internal fun navigationOverride(runVersionManager: RunVersionManager): NavigationOverride =
        object : NavigationOverride {
            override val priority = 10

            override fun override(oldBackstack: Backstack, targetBackstack: Backstack): List<IntroScreen>? =
                allowDiskReadsInStrictMode {
                    // TODO This should not run on the main thread
                    when {
                        targetBackstack.isNotEmpty() &&
                            targetBackstack.none { it.name == ScreenName.GooglePlayServices } &&
                            runVersionManager.isFirstRun ->
                            listOf(IntroScreen(targetBackstack))
                        else -> null
                    }
                }
        }
}
