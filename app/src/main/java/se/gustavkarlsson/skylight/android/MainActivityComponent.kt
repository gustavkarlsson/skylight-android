package se.gustavkarlsson.skylight.android

import me.tatarka.inject.annotations.Component
import se.gustavkarlsson.skylight.android.lib.navigation.NavigationComponent
import se.gustavkarlsson.skylight.android.lib.scopedservice.ScopedServiceComponent

@Component
internal abstract class MainActivityComponent(
    @Component val navigationComponent: NavigationComponent,
    @Component val scopedServiceComponent: ScopedServiceComponent,
) {
    abstract val renderer: Renderer

    companion object {
        val instance: MainActivityComponent = MainActivityComponent::class.create(
            navigationComponent = NavigationComponent.instance,
            scopedServiceComponent = ScopedServiceComponent.instance,
        )
    }
}
