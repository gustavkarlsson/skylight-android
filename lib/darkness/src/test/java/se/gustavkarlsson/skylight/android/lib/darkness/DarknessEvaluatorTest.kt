package se.gustavkarlsson.skylight.android.lib.darkness

import assertk.assertThat
import assertk.assertions.isBetween
import assertk.assertions.isEqualTo
import org.junit.Test
import se.gustavkarlsson.skylight.android.core.entities.Chance
import se.gustavkarlsson.skylight.android.core.entities.Chance.Companion.IMPOSSIBLE
import se.gustavkarlsson.skylight.android.core.entities.Chance.Companion.MAX
import kotlin.time.Instant

class DarknessEvaluatorTest {

    private val timestamp = Instant.fromEpochMilliseconds(0)
    private val impl = DarknessEvaluator

    @Test
    fun _0ZenithAngleEvaluatesToImpossible() {
        val chance = impl.evaluate(Darkness(0.0, timestamp))

        assertThat(chance).isEqualTo(IMPOSSIBLE)
    }

    @Test
    fun _90ZenithAngleEvaluatesToImpossible() {
        val chance = impl.evaluate(Darkness(90.0, timestamp))

        assertThat(chance).isEqualTo(IMPOSSIBLE)
    }

    @Test
    fun _180ZenithAngleEvaluatesToMax() {
        val chance = impl.evaluate(Darkness(180.0, timestamp))

        assertThat(chance).isEqualTo(MAX)
    }

    @Test
    fun minus180ZenithAngleEvaluatesToMax() {
        val chance = impl.evaluate(Darkness(-180.0, timestamp))

        assertThat(chance).isEqualTo(MAX)
    }

    @Test
    fun minus360ZenithAngleEvaluatesToMax() {
        val chance = impl.evaluate(Darkness(-360.0, timestamp))

        assertThat(chance).isEqualTo(MAX)
    }

    @Test
    fun _100ZenithAngleEvaluatesToMediumChance() {
        val chance = impl.evaluate(Darkness(100.0, timestamp))

        assertThat(chance).isBetween(Chance(0.2), Chance(0.4))
    }
}
