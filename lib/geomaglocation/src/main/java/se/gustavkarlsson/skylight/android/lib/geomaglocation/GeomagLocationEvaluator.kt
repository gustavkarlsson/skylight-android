package se.gustavkarlsson.skylight.android.lib.geomaglocation

import kotlin.math.abs
import se.gustavkarlsson.skylight.android.entities.Chance
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator

internal object GeomagLocationEvaluator : ChanceEvaluator<GeomagLocation> {

    override fun evaluate(value: GeomagLocation): Chance {
        val latitude = value.latitude
        val absoluteLatitude = abs(latitude)
        var chance = ((1.0 / FLEX) * absoluteLatitude) - ((BEST - FLEX) / FLEX)
        if (chance > 1.0) {
            chance = 2.0 - chance
        }
        return Chance(chance)
    }

    internal const val BEST = 67.0
    internal const val FLEX = 13.0
}
