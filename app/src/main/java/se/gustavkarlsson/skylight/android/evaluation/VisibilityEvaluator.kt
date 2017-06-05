package se.gustavkarlsson.skylight.android.evaluation

import dagger.Reusable
import se.gustavkarlsson.skylight.android.models.factors.Visibility
import javax.inject.Inject

@Reusable
class VisibilityEvaluator
@Inject
internal constructor() : ChanceEvaluator<Visibility> {

    override fun evaluate(value: Visibility): Chance {
        val clouds = value.cloudPercentage ?: return Chance.UNKNOWN
		val chance = -1.0 / 50.0 * clouds.toDouble() + 1.0
		return Chance.of(chance)
    }
}
