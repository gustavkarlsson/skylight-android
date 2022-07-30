package se.gustavkarlsson.skylight.android.feature.intro

import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoSet
import se.gustavkarlsson.skylight.android.core.AppScopeMarker
import se.gustavkarlsson.skylight.android.lib.navigation.NavigationOverride

@Module
@ContributesTo(AppScopeMarker::class)
object FeatureIntroModule {

    @Provides
    @IntoSet
    internal fun navigationOverride(impl: IntroNavigationOverride): NavigationOverride = impl
}
