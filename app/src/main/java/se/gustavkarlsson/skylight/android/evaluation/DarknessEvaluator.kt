package se.gustavkarlsson.skylight.android.evaluation

import dagger.Reusable
import se.gustavkarlsson.skylight.android.evaluation.Chance.Companion.UNKNOWN
import se.gustavkarlsson.skylight.android.models.factors.Darkness
import javax.inject.Inject

@Reusable
internal class DarknessEvaluator
@Inject
constructor() : ChanceEvaluator<Darkness> {

    override fun evaluate(value: Darkness): Chance {
        val zenithAngle = value.sunZenithAngle ?: return UNKNOWN
        return Chance(1.0 / 12.0 * zenithAngle - 8.0) // 96-108
    }
}
