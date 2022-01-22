package se.gustavkarlsson.skylight.android.lib.geomaglocation

import com.squareup.anvil.annotations.ContributesTo
import se.gustavkarlsson.skylight.android.core.AppScopeMarker
import se.gustavkarlsson.skylight.android.core.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.core.services.Formatter

@ContributesTo(AppScopeMarker::class)
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
