package se.gustavkarlsson.skylight.android.services_impl.evaluation

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import se.gustavkarlsson.skylight.android.entities.Visibility
import se.gustavkarlsson.skylight.android.entities.Chance
import se.gustavkarlsson.skylight.android.entities.Chance.Companion.IMPOSSIBLE
import se.gustavkarlsson.skylight.android.entities.Chance.Companion.MAX
import se.gustavkarlsson.skylight.android.entities.Chance.Companion.UNKNOWN

class VisibilityEvaluatorTest {

    lateinit var impl: VisibilityEvaluator

    @Before
    fun setUp() {
        impl = VisibilityEvaluator()
    }

    @Test
    fun nullCloudPercentageEvaluatesToUnknown() {
        val chance = impl.evaluate(Visibility(null))

        assertThat(chance).isEqualTo(UNKNOWN)
    }

    @Test
    fun _0CloudPercentageEvaluatesToMax() {
        val chance = impl.evaluate(Visibility(0))

        assertThat(chance).isEqualTo(MAX)
    }

    @Test
    fun _100CloudPercentageEvaluatesToImpossible() {
        val chance = impl.evaluate(Visibility(100))

        assertThat(chance).isEqualTo(IMPOSSIBLE)
    }

    @Test
    fun minus1CloudPercentageEvaluatesToUnknown() {
        val chance = impl.evaluate(Visibility(-1))

        assertThat(chance).isEqualTo(UNKNOWN)
    }

    @Test
    fun _101CloudPercentageEvaluatesToUnknown() {
        val chance = impl.evaluate(Visibility(101))

        assertThat(chance).isEqualTo(UNKNOWN)
    }

    @Test
    fun _50CloudPercentageEvaluatesToMediumChance() {
        val chance = impl.evaluate(Visibility(25))

        assertThat(chance).isBetween(Chance(0.4), Chance(0.6))
    }
}
