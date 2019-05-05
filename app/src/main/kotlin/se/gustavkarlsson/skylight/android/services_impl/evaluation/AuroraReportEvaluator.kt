package se.gustavkarlsson.skylight.android.services_impl.evaluation

import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.entities.Chance
import se.gustavkarlsson.skylight.android.entities.Darkness
import se.gustavkarlsson.skylight.android.entities.GeomagLocation
import se.gustavkarlsson.skylight.android.entities.KpIndex
import se.gustavkarlsson.skylight.android.entities.Weather
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator

class AuroraReportEvaluator(
	private val kpIndexEvaluator: ChanceEvaluator<KpIndex>,
	private val geomagLocationEvaluator: ChanceEvaluator<GeomagLocation>,
	private val weatherEvaluator: ChanceEvaluator<Weather>,
	private val darknessEvaluator: ChanceEvaluator<Darkness>
) : ChanceEvaluator<AuroraReport> {

	override fun evaluate(value: AuroraReport): Chance {
		val activityChance = value.kpIndex.value
			?.let(kpIndexEvaluator::evaluate) ?: Chance.UNKNOWN
		val locationChance = value.geomagLocation.value
			?.let(geomagLocationEvaluator::evaluate) ?: Chance.UNKNOWN
		val weatherChance = value.weather.value
			?.let(weatherEvaluator::evaluate) ?: Chance.UNKNOWN
		val darknessChance = value.darkness.value
			?.let(darknessEvaluator::evaluate) ?: Chance.UNKNOWN

		val chances = listOf(activityChance, locationChance, weatherChance, darknessChance)

		if (chances.any { !it.isKnown }) {
			return Chance.UNKNOWN
		}

		if (chances.any { !it.isPossible }) {
			return Chance.IMPOSSIBLE
		}

		return listOf(weatherChance, darknessChance, activityChance * locationChance).min()!!
	}
}
