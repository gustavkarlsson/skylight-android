package se.gustavkarlsson.skylight.android.lib.darkness

import kotlin.math.abs
import se.gustavkarlsson.skylight.android.core.entities.Chance
import se.gustavkarlsson.skylight.android.core.services.ChanceEvaluator

internal object DarknessEvaluator : ChanceEvaluator<Darkness> {

    // Technique explained here: https://stackoverflow.com/a/7869457/940731
    override fun evaluate(value: Darkness): Chance {
        val zenithAngle = value.sunZenithAngle
        val smallestZenithAnglePositive = abs((zenithAngle + 180) % 360 - 180)
        return Chance(1.0 / 12.0 * smallestZenithAnglePositive - 8.0) // 96-108
    }
}
