package se.gustavkarlsson.skylight.android.services_impl.evaluation

import dagger.Reusable
import se.gustavkarlsson.skylight.android.services.evaluation.Chance.Companion.UNKNOWN
import se.gustavkarlsson.skylight.android.entities.Darkness
import se.gustavkarlsson.skylight.android.services.evaluation.Chance
import se.gustavkarlsson.skylight.android.services.evaluation.ChanceEvaluator
import javax.inject.Inject

@Reusable
class DarknessEvaluator
@Inject
constructor() : ChanceEvaluator<Darkness> {

    override fun evaluate(value: Darkness): Chance {
        val zenithAngle = value.sunZenithAngle ?: return UNKNOWN
        return Chance(1.0 / 12.0 * zenithAngle - 8.0) // 96-108
    }
}
