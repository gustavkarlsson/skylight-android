package se.gustavkarlsson.skylight.android.lib.places

import com.squareup.anvil.annotations.ContributesTo
import se.gustavkarlsson.skylight.android.core.AppScopeMarker

@ContributesTo(AppScopeMarker::class)
interface PlacesComponent {

    fun placesRepository(): PlacesRepository

    fun selectedPlaceRepository(): SelectedPlaceRepository

    interface Setter {
        fun setPlacesComponent(component: PlacesComponent) {
            instance = component
        }
    }

    companion object {
        lateinit var instance: PlacesComponent
            private set
    }
}
