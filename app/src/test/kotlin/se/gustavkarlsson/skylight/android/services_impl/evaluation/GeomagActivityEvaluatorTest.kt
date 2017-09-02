package se.gustavkarlsson.skylight.android.services_impl.evaluation

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import se.gustavkarlsson.skylight.android.entities.GeomagActivity
import se.gustavkarlsson.skylight.android.services.evaluation.Chance
import se.gustavkarlsson.skylight.android.services.evaluation.Chance.Companion.IMPOSSIBLE
import se.gustavkarlsson.skylight.android.services.evaluation.Chance.Companion.MAX
import se.gustavkarlsson.skylight.android.services.evaluation.Chance.Companion.UNKNOWN

class GeomagActivityEvaluatorTest {

    lateinit var impl: GeomagActivityEvaluator

    @Before
    fun setUp() {
        impl = GeomagActivityEvaluator()
    }

    @Test
    fun nullKpIndexEvaluatesToUnknown() {
        val chance = impl.evaluate(GeomagActivity(null))

        assertThat(chance).isEqualTo(UNKNOWN)
    }

    @Test
    fun _0KpIndexEvaluatesToImpossible() {
        val chance = impl.evaluate(GeomagActivity(0.0))

        assertThat(chance).isEqualTo(IMPOSSIBLE)
    }

    @Test
    fun _9KpIndexEvaluatesToMax() {
        val chance = impl.evaluate(GeomagActivity(9.0))

        assertThat(chance).isEqualTo(MAX)
    }

    @Test
    fun _5KpIndexEvaluatesToMediumChance() {
        val chance = impl.evaluate(GeomagActivity(5.0))

        assertThat(chance).isBetween(Chance(0.4), Chance(0.6))
    }

    @Test
    fun minus1KpIndexEvaluatesToUnknown() {
        val chance = impl.evaluate(GeomagActivity(-1.0))

        assertThat(chance).isEqualTo(UNKNOWN)
    }

    @Test
    fun _10KpIndexEvaluatesToUnknown() {
        val chance = impl.evaluate(GeomagActivity(10.0))

        assertThat(chance).isEqualTo(UNKNOWN)
    }
}
