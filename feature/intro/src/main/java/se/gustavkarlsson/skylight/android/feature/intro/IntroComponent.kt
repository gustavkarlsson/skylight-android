package se.gustavkarlsson.skylight.android.feature.intro

import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import se.gustavkarlsson.skylight.android.lib.navigation.NavigationOverride
import se.gustavkarlsson.skylight.android.lib.runversion.RunVersionComponent

@Component
abstract class IntroComponent(
    @Component internal val runVersionComponent: RunVersionComponent,
) {
    abstract val navigationOverride: NavigationOverride

    @Provides
    internal fun navigationOverride(impl: IntroNavigationOverride): NavigationOverride = impl

    companion object {
        val instance: IntroComponent = IntroComponent::class.create(
            runVersionComponent = RunVersionComponent.instance,
        )
    }
}
