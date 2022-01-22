package se.gustavkarlsson.skylight.android.feature.intro

import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.multibindings.IntoSet
import se.gustavkarlsson.skylight.android.lib.navigation.NavigationOverride

@Module
object FeatureIntroModule {

    @Provides
    @Reusable
    @IntoSet
    internal fun navigationOverride(impl: IntroNavigationOverride): NavigationOverride = impl
}
