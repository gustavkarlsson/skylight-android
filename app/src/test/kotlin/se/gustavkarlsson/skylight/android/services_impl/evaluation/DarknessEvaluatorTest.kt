package se.gustavkarlsson.skylight.android.services_impl.evaluation
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import se.gustavkarlsson.skylight.android.entities.Darkness
import se.gustavkarlsson.skylight.android.services.evaluation.Chance
import se.gustavkarlsson.skylight.android.services.evaluation.Chance.Companion.IMPOSSIBLE
import se.gustavkarlsson.skylight.android.services.evaluation.Chance.Companion.MAX
import se.gustavkarlsson.skylight.android.services.evaluation.Chance.Companion.UNKNOWN

class DarknessEvaluatorTest {

    lateinit var impl: DarknessEvaluator

    @Before
    fun setUp() {
        impl = DarknessEvaluator()
    }

    @Test
    fun nullZenithAngleEvaluatesToUnknown() {
        val chance = impl.evaluate(Darkness(null))

		assertThat(chance).isEqualTo(UNKNOWN)
    }

    @Test
    fun _0ZenithAngleEvaluatesToImpossible() {
        val chance = impl.evaluate(Darkness(0.0))

        assertThat(chance).isEqualTo(IMPOSSIBLE)
    }

    @Test
    fun _90ZenithAngleEvaluatesToImpossible() {
        val chance = impl.evaluate(Darkness(90.0))

        assertThat(chance).isEqualTo(IMPOSSIBLE)
    }

    @Test
    fun _180ZenithAngleEvaluatesToMax() {
        val chance = impl.evaluate(Darkness(180.0))

        assertThat(chance).isEqualTo(MAX)
    }

    @Test
    fun minus180ZenithAngleEvaluatesToMax() {
        val chance = impl.evaluate(Darkness(-180.0))

        assertThat(chance).isEqualTo(MAX)
    }

    @Test
    fun minus360ZenithAngleEvaluatesToMax() {
        val chance = impl.evaluate(Darkness(-360.0))

        assertThat(chance).isEqualTo(MAX)
    }

    @Test
    fun _100ZenithAngleEvaluatesToMediumChance() {
        val chance = impl.evaluate(Darkness(100.0))

        assertThat(chance).isBetween(Chance(0.2), Chance(0.4))
    }
}
