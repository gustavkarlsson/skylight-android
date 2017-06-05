package se.gustavkarlsson.skylight.android.evaluation

import dagger.Reusable
import se.gustavkarlsson.skylight.android.models.AuroraReport
import se.gustavkarlsson.skylight.android.models.factors.Darkness
import se.gustavkarlsson.skylight.android.models.factors.GeomagActivity
import se.gustavkarlsson.skylight.android.models.factors.GeomagLocation
import se.gustavkarlsson.skylight.android.models.factors.Visibility
import javax.inject.Inject

@Reusable
class AuroraReportEvaluator
@Inject
internal constructor(
		private val geomagActivityEvaluator: ChanceEvaluator<GeomagActivity>,
		private val geomagLocationEvaluator: ChanceEvaluator<GeomagLocation>,
		private val visibilityEvaluator: ChanceEvaluator<Visibility>,
		private val darknessEvaluator: ChanceEvaluator<Darkness>
) : ChanceEvaluator<AuroraReport> {

	override fun evaluate(value: AuroraReport): Chance {
		val factors = value.factors
		val activityChance = geomagActivityEvaluator.evaluate(factors.geomagActivity)
		val locationChance = geomagLocationEvaluator.evaluate(factors.geomagLocation)
		val visibilityChance = visibilityEvaluator.evaluate(factors.visibility)
		val darknessChance = darknessEvaluator.evaluate(factors.darkness)

		val chances = listOf(activityChance, locationChance, visibilityChance, darknessChance)

		if (chances.any { !it.isKnown }) {
			return Chance.UNKNOWN
		}

		if (chances.any { !it.isPossible }) {
			return Chance.IMPOSSIBLE
		}

		val geomagActivityAndLocationChance = Chance.of(activityChance.value!! * locationChance.value!!)

		listOf(visibilityChance, darknessChance, geomagActivityAndLocationChance)
				.min()
				.let {
					return it!!
				}
	}
}
