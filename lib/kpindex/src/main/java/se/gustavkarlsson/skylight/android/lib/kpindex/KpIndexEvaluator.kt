package se.gustavkarlsson.skylight.android.lib.kpindex

import se.gustavkarlsson.skylight.android.core.entities.Chance
import se.gustavkarlsson.skylight.android.core.services.ChanceEvaluator

internal object KpIndexEvaluator : ChanceEvaluator<KpIndex> {

    override fun evaluate(value: KpIndex): Chance {
        val kpIndex = value.value
        val result = 0.1428571 * kpIndex - 0.1428571 // 1-8 maps to 0-1
        return Chance(result)
    }
}
