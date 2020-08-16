package se.gustavkarlsson.skylight.android.lib.geomaglocation

import se.gustavkarlsson.skylight.android.core.entities.Chance
import se.gustavkarlsson.skylight.android.core.services.ChanceEvaluator
import kotlin.math.abs

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
