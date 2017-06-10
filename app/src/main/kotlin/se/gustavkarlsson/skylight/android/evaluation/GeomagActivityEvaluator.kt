package se.gustavkarlsson.skylight.android.evaluation

import dagger.Reusable
import se.gustavkarlsson.skylight.android.evaluation.Chance.Companion.UNKNOWN
import se.gustavkarlsson.skylight.android.models.factors.GeomagActivity
import javax.inject.Inject

@Reusable
class GeomagActivityEvaluator
@Inject
constructor() : ChanceEvaluator<GeomagActivity> {

    override fun evaluate(value: GeomagActivity): Chance {
        val kpIndex = value.kpIndex ?: return UNKNOWN
		val chance = 1.0 / 9.0 * kpIndex + 0.0 // 0-9
		return Chance(chance)
    }
}
