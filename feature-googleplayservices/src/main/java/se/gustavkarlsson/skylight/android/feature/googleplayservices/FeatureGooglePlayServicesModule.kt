package se.gustavkarlsson.skylight.android.feature.googleplayservices

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.multibindings.IntoSet
import se.gustavkarlsson.skylight.android.navigation.Backstack
import se.gustavkarlsson.skylight.android.navigation.NavigationOverride

@Module
class FeatureGooglePlayServicesModule {

    @Provides
    @Reusable
    @IntoSet
    internal fun navigationOverride(context: Context): NavigationOverride =
        object : NavigationOverride {
            val googlePlayServicesChecker = GmsGooglePlayServicesChecker(context)

            override val priority = 8

            override fun override(backstack: Backstack): Backstack? =
                if (!googlePlayServicesChecker.isAvailable) {
                    listOf(GooglePlayServicesScreen(backstack))
                } else null
        }
}
