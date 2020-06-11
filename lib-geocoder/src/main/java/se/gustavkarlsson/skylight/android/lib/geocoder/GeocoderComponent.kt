package se.gustavkarlsson.skylight.android.lib.geocoder

interface GeocoderComponent {

    fun geocoder(): Geocoder

    interface Setter {
        fun setGeocoderComponent(component: GeocoderComponent) {
            instance = component
        }
    }

    companion object {
        lateinit var instance: GeocoderComponent
            private set
    }
}
