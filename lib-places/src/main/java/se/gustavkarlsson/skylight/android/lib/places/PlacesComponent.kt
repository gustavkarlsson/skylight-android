package se.gustavkarlsson.skylight.android.lib.places

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
