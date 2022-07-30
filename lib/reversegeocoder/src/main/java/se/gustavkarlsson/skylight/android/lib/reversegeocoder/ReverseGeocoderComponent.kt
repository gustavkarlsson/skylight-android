package se.gustavkarlsson.skylight.android.lib.reversegeocoder

import com.squareup.anvil.annotations.ContributesTo
import se.gustavkarlsson.skylight.android.core.AppScopeMarker

@ContributesTo(AppScopeMarker::class)
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
