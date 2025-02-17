package se.gustavkarlsson.skylight.android.feature.googleplayservices

import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides
import se.gustavkarlsson.skylight.android.core.CoreComponent
import se.gustavkarlsson.skylight.android.lib.navigation.NavigationOverride

@Component
abstract class GooglePlayServicesComponent(
    @Component internal val coreComponent: CoreComponent,
) {
    abstract val navigationOverride: NavigationOverride

    @Provides
    internal fun navigationOverride(impl: GooglePlayServicesNavigationOverride): NavigationOverride = impl

    companion object {
        val instance: GooglePlayServicesComponent = GooglePlayServicesComponent::class.create(
            coreComponent = CoreComponent.instance,
        )
    }
}
