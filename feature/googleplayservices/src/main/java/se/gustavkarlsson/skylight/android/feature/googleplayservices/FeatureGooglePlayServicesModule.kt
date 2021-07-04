package se.gustavkarlsson.skylight.android.feature.googleplayservices

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.multibindings.IntoSet
import se.gustavkarlsson.skylight.android.lib.navigation.Backstack
import se.gustavkarlsson.skylight.android.lib.navigation.NavigationOverride
import se.gustavkarlsson.skylight.android.lib.navigation.ScreenName

@Module
object FeatureGooglePlayServicesModule {

    @Provides
    @Reusable
    @IntoSet
    internal fun navigationOverride(context: Context): NavigationOverride =
        object : NavigationOverride {
            override val priority = 8

            private val REQUIRING_SCREEN_NAMES = setOf(ScreenName.Main)

            override fun override(
                oldBackstack: Backstack,
                targetBackstack: Backstack,
            ): List<GooglePlayServicesScreen>? {
                val googlePlayServicesChecker = GmsGooglePlayServicesChecker(context)
                return when {
                    targetBackstack.any { it.name in REQUIRING_SCREEN_NAMES } &&
                        targetBackstack.none { it.name == ScreenName.GooglePlayServices } &&
                        !googlePlayServicesChecker.isAvailable ->
                        listOf(GooglePlayServicesScreen(targetBackstack))
                    else -> null
                }
            }
        }
}
