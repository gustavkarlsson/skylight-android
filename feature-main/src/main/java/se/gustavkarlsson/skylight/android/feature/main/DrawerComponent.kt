package se.gustavkarlsson.skylight.android.feature.main

import dagger.Component
import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.AppComponent
import se.gustavkarlsson.skylight.android.feature.main.gui.drawer.DrawerViewModel
import se.gustavkarlsson.skylight.android.services.PlacesRepository
import se.gustavkarlsson.skylight.android.services.SelectedPlaceRepository

@Component(
    modules = [DrawerModule::class],
    dependencies = [AppComponent::class]
)
internal interface DrawerComponent {
    fun viewModel(): DrawerViewModel

    companion object {
        fun build(): DrawerComponent =
            DaggerDrawerComponent.builder()
                .appComponent(AppComponent.instance)
                .build()
    }
}

@Module
internal class DrawerModule {

    @Provides
    fun viewModel(
        placesRepository: PlacesRepository,
        selectedPlaceRepository: SelectedPlaceRepository
    ): DrawerViewModel = DrawerViewModel(
        placesRepository,
        selectedPlaceRepository
    )
}
