package se.gustavkarlsson.skylight.android.evaluation

import dagger.Reusable
import se.gustavkarlsson.skylight.android.evaluation.Chance.Companion.UNKNOWN
import se.gustavkarlsson.skylight.android.entities.Visibility
import javax.inject.Inject

@Reusable
class VisibilityEvaluator
@Inject
constructor() : ChanceEvaluator<Visibility> {

    override fun evaluate(value: Visibility): Chance {
        val clouds = value.cloudPercentage ?: return UNKNOWN
		val chance = -1.0 / 50.0 * clouds.toDouble() + 1.0
		return Chance(chance)
    }
}
