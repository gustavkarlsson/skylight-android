package se.gustavkarlsson.skylight.android.lib.kpindex

import assertk.assertThat
import assertk.assertions.isBetween
import assertk.assertions.isEqualTo
import org.junit.Test
import se.gustavkarlsson.skylight.android.core.entities.Chance
import se.gustavkarlsson.skylight.android.core.entities.Chance.Companion.IMPOSSIBLE
import se.gustavkarlsson.skylight.android.core.entities.Chance.Companion.MAX
import se.gustavkarlsson.skylight.android.core.entities.Chance.Companion.UNKNOWN
import kotlin.time.Instant

class KpIndexEvaluatorTest {

    private val timestamp = Instant.fromEpochMilliseconds(0)
    private val impl = KpIndexEvaluator

    @Test
    fun _0KpIndexEvaluatesToImpossible() {
        val chance = impl.evaluate(KpIndex(0.0, timestamp))

        assertThat(chance).isEqualTo(IMPOSSIBLE)
    }

    @Test
    fun _9KpIndexEvaluatesToMax() {
        val chance = impl.evaluate(KpIndex(9.0, timestamp))

        assertThat(chance).isEqualTo(MAX)
    }

    @Test
    fun _4KpIndexEvaluatesToMediumChance() {
        val chance = impl.evaluate(KpIndex(4.0, timestamp))

        assertThat(chance).isBetween(Chance(0.4), Chance(0.5))
    }

    @Test
    fun minus1KpIndexEvaluatesToUnknown() {
        val chance = impl.evaluate(KpIndex(-1.0, timestamp))

        assertThat(chance).isEqualTo(UNKNOWN)
    }

    @Test
    fun _10KpIndexEvaluatesToUnknown() {
        val chance = impl.evaluate(KpIndex(10.0, timestamp))

        assertThat(chance).isEqualTo(UNKNOWN)
    }
}
