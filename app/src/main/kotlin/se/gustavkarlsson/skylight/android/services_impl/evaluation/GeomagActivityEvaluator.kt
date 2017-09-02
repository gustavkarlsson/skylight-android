package se.gustavkarlsson.skylight.android.services_impl.evaluation

import dagger.Reusable
import se.gustavkarlsson.skylight.android.entities.GeomagActivity
import se.gustavkarlsson.skylight.android.services.evaluation.Chance
import se.gustavkarlsson.skylight.android.services.evaluation.Chance.Companion.UNKNOWN
import se.gustavkarlsson.skylight.android.services.evaluation.ChanceEvaluator
import javax.inject.Inject

@Reusable
class GeomagActivityEvaluator
@Inject
constructor() : ChanceEvaluator<GeomagActivity> {

    override fun evaluate(value: GeomagActivity): Chance {
        val kpIndex = value.kpIndex ?: return UNKNOWN
		if (kpIndex < 0 || kpIndex > 9) {
			return UNKNOWN
        }
		val chance = 1.0 / 9.0 * kpIndex + 0.0 // 0-9
		return Chance(chance)
    }
}
