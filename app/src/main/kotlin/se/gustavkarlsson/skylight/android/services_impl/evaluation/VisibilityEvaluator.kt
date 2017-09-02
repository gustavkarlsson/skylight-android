package se.gustavkarlsson.skylight.android.services_impl.evaluation

import dagger.Reusable
import se.gustavkarlsson.skylight.android.entities.Visibility
import se.gustavkarlsson.skylight.android.services.evaluation.Chance
import se.gustavkarlsson.skylight.android.services.evaluation.Chance.Companion.UNKNOWN
import se.gustavkarlsson.skylight.android.services.evaluation.ChanceEvaluator
import javax.inject.Inject

@Reusable
class VisibilityEvaluator
@Inject
constructor() : ChanceEvaluator<Visibility> {

    override fun evaluate(value: Visibility): Chance {
        val clouds = value.cloudPercentage ?: return UNKNOWN
		if (clouds < 0 || clouds > 100) {
			return UNKNOWN
        }
		val chance = -1.0 / 50.0 * clouds.toDouble() + 1.0
		return Chance(chance)
    }
}
