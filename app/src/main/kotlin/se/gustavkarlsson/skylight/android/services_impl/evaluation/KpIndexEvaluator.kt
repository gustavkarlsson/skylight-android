package se.gustavkarlsson.skylight.android.services_impl.evaluation

import dagger.Reusable
import se.gustavkarlsson.skylight.android.entities.Chance
import se.gustavkarlsson.skylight.android.entities.Chance.Companion.UNKNOWN
import se.gustavkarlsson.skylight.android.entities.KpIndex
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator
import javax.inject.Inject

@Reusable
class KpIndexEvaluator
@Inject
constructor() : ChanceEvaluator<KpIndex> {

    override fun evaluate(value: KpIndex): Chance {
        val kpIndex = value.value ?: return UNKNOWN
		if (kpIndex < 0 || kpIndex > 9) {
			return UNKNOWN
        }
		val chance = 1.0 / 9.0 * kpIndex + 0.0 // 0-9
		return Chance(chance)
    }
}
