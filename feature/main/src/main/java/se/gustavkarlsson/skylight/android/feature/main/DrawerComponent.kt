package se.gustavkarlsson.skylight.android.feature.main

import dagger.Component
import se.gustavkarlsson.skylight.android.core.AppComponent
import se.gustavkarlsson.skylight.android.feature.main.gui.drawer.DrawerViewModel
import se.gustavkarlsson.skylight.android.lib.places.PlacesComponent

@Component(
    dependencies = [
        AppComponent::class,
        PlacesComponent::class
    ]
)
internal interface DrawerComponent {
    fun viewModel(): DrawerViewModel

    companion object {
        fun build(): DrawerComponent =
            DaggerDrawerComponent.builder()
                .appComponent(AppComponent.instance)
                .placesComponent(PlacesComponent.instance)
                .build()
    }
}
