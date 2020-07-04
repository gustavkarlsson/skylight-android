package se.gustavkarlsson.skylight.android.lib.geomaglocation

import se.gustavkarlsson.skylight.android.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.Formatter

interface GeomagLocationComponent {

    fun geomagLocationChanceEvaluator(): ChanceEvaluator<GeomagLocation>

    fun geomagLocationFormatter(): Formatter<GeomagLocation>

    interface Setter {
        fun setGeomagLocationComponent(component: GeomagLocationComponent) {
            instance = component
        }
    }

    companion object {
        lateinit var instance: GeomagLocationComponent
            private set
    }
}
