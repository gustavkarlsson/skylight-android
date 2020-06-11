package se.gustavkarlsson.skylight.android.lib.weather

import se.gustavkarlsson.skylight.android.entities.Chance
import se.gustavkarlsson.skylight.android.entities.Chance.Companion.UNKNOWN
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator

internal object WeatherEvaluator : ChanceEvaluator<Weather> {

    override fun evaluate(value: Weather): Chance {
        val clouds = value.cloudPercentage
        if (clouds < 0 || clouds > 100) {
            return UNKNOWN
        }
        val chance = -1.0 / 50.0 * clouds.toDouble() + 1.0
        return Chance(chance)
    }
}
