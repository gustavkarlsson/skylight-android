package se.gustavkarlsson.skylight.android.services_impl.evaluation

import assertk.assert
import assertk.assertions.isBetween
import assertk.assertions.isEqualTo
import org.junit.Before
import org.junit.Test
import se.gustavkarlsson.skylight.android.entities.Chance
import se.gustavkarlsson.skylight.android.entities.Chance.Companion.IMPOSSIBLE
import se.gustavkarlsson.skylight.android.entities.Chance.Companion.MAX
import se.gustavkarlsson.skylight.android.entities.Chance.Companion.UNKNOWN
import se.gustavkarlsson.skylight.android.entities.Visibility

class VisibilityEvaluatorTest {

    lateinit var impl: VisibilityEvaluator

    @Before
    fun setUp() {
        impl = VisibilityEvaluator()
    }

    @Test
    fun nullCloudPercentageEvaluatesToUnknown() {
        val chance = impl.evaluate(Visibility(null))

        assert(chance).isEqualTo(UNKNOWN)
    }

    @Test
    fun _0CloudPercentageEvaluatesToMax() {
        val chance = impl.evaluate(Visibility(0))

        assert(chance).isEqualTo(MAX)
    }

    @Test
    fun _100CloudPercentageEvaluatesToImpossible() {
        val chance = impl.evaluate(Visibility(100))

        assert(chance).isEqualTo(IMPOSSIBLE)
    }

    @Test
    fun minus1CloudPercentageEvaluatesToUnknown() {
        val chance = impl.evaluate(Visibility(-1))

        assert(chance).isEqualTo(UNKNOWN)
    }

    @Test
    fun _101CloudPercentageEvaluatesToUnknown() {
        val chance = impl.evaluate(Visibility(101))

        assert(chance).isEqualTo(UNKNOWN)
    }

    @Test
    fun _50CloudPercentageEvaluatesToMediumChance() {
        val chance = impl.evaluate(Visibility(25))

		assertk.assert(chance).isBetween(Chance(0.4), Chance(0.6))
    }
}
