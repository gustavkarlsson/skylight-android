package se.gustavkarlsson.skylight.android.lib.geocoder

import com.squareup.anvil.annotations.ContributesTo
import se.gustavkarlsson.skylight.android.core.AppScopeMarker

@ContributesTo(AppScopeMarker::class)
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
