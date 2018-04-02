package se.gustavkarlsson.skylight.android.services_impl.evaluation

import org.amshove.kluent.shouldEqual
import org.junit.Before
import org.junit.Test
import se.gustavkarlsson.skylight.android.entities.Chance
import se.gustavkarlsson.skylight.android.entities.Chance.Companion.IMPOSSIBLE
import se.gustavkarlsson.skylight.android.entities.Chance.Companion.MAX
import se.gustavkarlsson.skylight.android.entities.Chance.Companion.UNKNOWN
import se.gustavkarlsson.skylight.android.entities.Visibility
import se.gustavkarlsson.skylight.android.test.shouldBeInRange

class VisibilityEvaluatorTest {

    lateinit var impl: VisibilityEvaluator

    @Before
    fun setUp() {
        impl = VisibilityEvaluator()
    }

    @Test
    fun nullCloudPercentageEvaluatesToUnknown() {
        val chance = impl.evaluate(Visibility(null))

        chance shouldEqual UNKNOWN
    }

    @Test
    fun _0CloudPercentageEvaluatesToMax() {
        val chance = impl.evaluate(Visibility(0))

        chance shouldEqual MAX
    }

    @Test
    fun _100CloudPercentageEvaluatesToImpossible() {
        val chance = impl.evaluate(Visibility(100))

        chance shouldEqual IMPOSSIBLE
    }

    @Test
    fun minus1CloudPercentageEvaluatesToUnknown() {
        val chance = impl.evaluate(Visibility(-1))

        chance shouldEqual UNKNOWN
    }

    @Test
    fun _101CloudPercentageEvaluatesToUnknown() {
        val chance = impl.evaluate(Visibility(101))

        chance shouldEqual UNKNOWN
    }

    @Test
    fun _50CloudPercentageEvaluatesToMediumChance() {
        val chance = impl.evaluate(Visibility(25))

		chance shouldBeInRange Chance(0.4)..Chance(0.6)
    }
}
