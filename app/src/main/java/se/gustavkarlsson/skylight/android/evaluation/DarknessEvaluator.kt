package se.gustavkarlsson.skylight.android.evaluation

import dagger.Reusable
import se.gustavkarlsson.skylight.android.models.factors.Darkness
import javax.inject.Inject

@Reusable
class DarknessEvaluator
@Inject
internal constructor() : ChanceEvaluator<Darkness> {

    override fun evaluate(value: Darkness): Chance {
        val zenithAngle = value.sunZenithAngle ?: return Chance.unknown()
        return Chance.of(1.0 / 12.0 * zenithAngle - 8.0) // 96-108
    }
}
