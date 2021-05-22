package se.gustavkarlsson.skylight.android.lib.reversegeocoder

interface ReverseGeocoderComponent {

    fun reverseGeocoder(): ReverseGeocoder

    interface Setter {
        fun setReverseGeocoderComponent(component: ReverseGeocoderComponent) {
            instance = component
        }
    }

    companion object {
        lateinit var instance: ReverseGeocoderComponent
            private set
    }
}
