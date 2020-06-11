package se.gustavkarlsson.skylight.android.lib.kpindex

import assertk.assert
import assertk.assertions.isBetween
import assertk.assertions.isEqualTo
import org.junit.Test
import se.gustavkarlsson.skylight.android.entities.Chance
import se.gustavkarlsson.skylight.android.entities.Chance.Companion.IMPOSSIBLE
import se.gustavkarlsson.skylight.android.entities.Chance.Companion.MAX
import se.gustavkarlsson.skylight.android.entities.Chance.Companion.UNKNOWN

class KpIndexEvaluatorTest {

    private val impl = KpIndexEvaluator

    @Test
    fun _0KpIndexEvaluatesToImpossible() {
        val chance = impl.evaluate(KpIndex(0.0))

        assert(chance).isEqualTo(IMPOSSIBLE)
    }

    @Test
    fun _9KpIndexEvaluatesToMax() {
        val chance = impl.evaluate(KpIndex(9.0))

        assert(chance).isEqualTo(MAX)
    }

    @Test
    fun _4KpIndexEvaluatesToMediumChance() {
        val chance = impl.evaluate(KpIndex(4.0))

        assert(chance).isBetween(Chance(0.6), Chance(0.8))
    }

    @Test
    fun minus1KpIndexEvaluatesToUnknown() {
        val chance = impl.evaluate(KpIndex(-1.0))

        assert(chance).isEqualTo(UNKNOWN)
    }

    @Test
    fun _10KpIndexEvaluatesToUnknown() {
        val chance = impl.evaluate(KpIndex(10.0))

        assert(chance).isEqualTo(UNKNOWN)
    }
}
