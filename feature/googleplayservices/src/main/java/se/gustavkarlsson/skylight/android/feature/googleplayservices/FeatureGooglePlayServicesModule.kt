package se.gustavkarlsson.skylight.android.feature.googleplayservices

import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import se.gustavkarlsson.skylight.android.core.AppScopeMarker
import se.gustavkarlsson.skylight.android.lib.navigation.NavigationOverride

@Module
@ContributesTo(AppScopeMarker::class)
object FeatureGooglePlayServicesModule {

    @Provides
    @IntoSet
    internal fun navigationOverride(impl: GooglePlayServicesNavigationOverride): NavigationOverride = impl
}
