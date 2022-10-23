package se.gustavkarlsson.skylight.android.lib.aurora

import dagger.Reusable
import se.gustavkarlsson.skylight.android.core.entities.Chance
import se.gustavkarlsson.skylight.android.core.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.lib.darkness.Darkness
import se.gustavkarlsson.skylight.android.lib.geomaglocation.GeomagLocation
import se.gustavkarlsson.skylight.android.lib.kpindex.KpIndex
import se.gustavkarlsson.skylight.android.lib.weather.Weather
import javax.inject.Inject

@Reusable
internal class AuroraReportEvaluator @Inject constructor(
    private val kpIndexEvaluator: ChanceEvaluator<KpIndex>,
    private val geomagLocationEvaluator: ChanceEvaluator<GeomagLocation>,
    private val weatherEvaluator: ChanceEvaluator<Weather>,
    private val darknessEvaluator: ChanceEvaluator<Darkness>,
) : ChanceEvaluator<AuroraReport> {

    override fun evaluate(value: AuroraReport): Chance {
        val activityChance = value.kpIndex.fold(
            ifLeft = { Chance.UNKNOWN },
            ifRight = { kpIndex -> kpIndexEvaluator.evaluate(kpIndex) },
        )
        val locationChance = geomagLocationEvaluator.evaluate(value.geomagLocation)
        val weatherChance = value.weather.fold(
            ifLeft = { Chance.UNKNOWN },
            ifRight = { weather -> weatherEvaluator.evaluate(weather) },
        )
        val darknessChance = darknessEvaluator.evaluate(value.darkness)

        val chances = listOf(activityChance, locationChance, weatherChance, darknessChance)

        if (chances.any { !it.isKnown }) {
            return Chance.UNKNOWN
        }

        if (chances.any { !it.isPossible }) {
            return Chance.IMPOSSIBLE
        }

        return listOf(weatherChance, darknessChance, activityChance * locationChance).minOrNull()!!
    }
}
