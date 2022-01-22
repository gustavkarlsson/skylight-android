package se.gustavkarlsson.skylight.android.feature.googleplayservices

import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import se.gustavkarlsson.skylight.android.lib.navigation.NavigationOverride

@Module
object FeatureGooglePlayServicesModule {

    @Provides
    @IntoSet
    internal fun navigationOverride(impl: GooglePlayServicesNavigationOverride): NavigationOverride = impl
}
