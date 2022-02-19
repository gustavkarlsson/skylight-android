package se.gustavkarlsson.skylight.android.lib.weather

import se.gustavkarlsson.skylight.android.core.entities.Chance
import se.gustavkarlsson.skylight.android.core.entities.Chance.Companion.UNKNOWN
import se.gustavkarlsson.skylight.android.core.services.ChanceEvaluator

internal object WeatherEvaluator : ChanceEvaluator<Weather> {

    override fun evaluate(value: Weather): Chance {
        val clouds = value.cloudPercentage
        if (clouds < 0 || clouds > 100) {
            return UNKNOWN
        }
        val chance = -1.0 / 50.0 * clouds.toDouble() + 1.0 // 50-0 maps to 0-1
        return Chance(chance)
    }
}
