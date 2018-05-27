package se.gustavkarlsson.skylight.android.services_impl.evaluation

import se.gustavkarlsson.skylight.android.entities.*
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator

class AuroraReportEvaluator(
	private val kpIndexEvaluator: ChanceEvaluator<KpIndex>,
	private val geomagLocationEvaluator: ChanceEvaluator<GeomagLocation>,
	private val weatherEvaluator: ChanceEvaluator<Weather>,
	private val darknessEvaluator: ChanceEvaluator<Darkness>
) : ChanceEvaluator<AuroraReport> {

	override fun evaluate(value: AuroraReport): Chance {
		val factors = value.factors
		val activityChance = kpIndexEvaluator.evaluate(factors.kpIndex)
		val locationChance = geomagLocationEvaluator.evaluate(factors.geomagLocation)
		val weatherChance = weatherEvaluator.evaluate(factors.weather)
		val darknessChance = darknessEvaluator.evaluate(factors.darkness)

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
