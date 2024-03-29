package se.gustavkarlsson.skylight.android.lib.darkness

import se.gustavkarlsson.skylight.android.core.entities.Chance
import se.gustavkarlsson.skylight.android.core.services.ChanceEvaluator
import kotlin.math.abs

internal object DarknessEvaluator : ChanceEvaluator<Darkness> {

    // Technique explained here: https://stackoverflow.com/a/7869457/940731
    override fun evaluate(value: Darkness): Chance {
        val smallestZenithAnglePositive = abs((value.sunZenithAngle + 180) % 360 - 180) // outcome: 0-180
        val chance = 1.0 / 12.0 * smallestZenithAnglePositive - 8.0 // 96-108 maps to 0-1
        return Chance(chance)
    }
}
