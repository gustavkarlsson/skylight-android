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
            val googlePlayServicesChecker = GmsGooglePlayServicesChecker(context)

            override val priority = 8

            override fun override(oldBackstack: Backstack, targetBackstack: Backstack) =
                when {
                    targetBackstack.isNotEmpty() &&
                        targetBackstack.none { it.name == ScreenName.Intro } &&
                        !googlePlayServicesChecker.isAvailable ->
                        listOf(GooglePlayServicesScreen(targetBackstack))
                    else -> null
                }
        }
}
