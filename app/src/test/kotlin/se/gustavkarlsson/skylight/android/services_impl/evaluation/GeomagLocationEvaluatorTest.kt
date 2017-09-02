package se.gustavkarlsson.skylight.android.services_impl.evaluation

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import se.gustavkarlsson.skylight.android.entities.GeomagLocation
import se.gustavkarlsson.skylight.android.services.evaluation.Chance

class GeomagLocationEvaluatorTest {

    lateinit var impl: GeomagLocationEvaluator

    @Before
    fun setUp() {
        impl = GeomagLocationEvaluator()
    }

    @Test
    fun nullLatitudeEvaluatesToUnknown() {
        val chance = impl.evaluate(GeomagLocation(null))

        assertThat(chance).isEqualTo(Chance.UNKNOWN)
    }

    @Test
    fun _0LatitudeEvaluatesToImpossible() {
        val chance = impl.evaluate(GeomagLocation(0.0))

        assertThat(chance).isEqualTo(Chance.IMPOSSIBLE)
    }

    @Test
    fun _90LatitudeEvaluatesToImpossible() {
        val chance = impl.evaluate(GeomagLocation(90.0))

        assertThat(chance).isEqualTo(Chance.IMPOSSIBLE)
    }

    @Test
    fun minus90LatitudeEvaluatesToImpossible() {
        val chance = impl.evaluate(GeomagLocation(-90.0))

        assertThat(chance).isEqualTo(Chance.IMPOSSIBLE)
    }

    @Test
    fun _67LatitudeEvaluatesToMax() {
        val chance = impl.evaluate(GeomagLocation(67.0))

        assertThat(chance).isEqualTo(Chance.MAX)
    }

    @Test
    fun minus67LatitudeEvaluatesToMax() {
        val chance = impl.evaluate(GeomagLocation(-67.0))

        assertThat(chance).isEqualTo(Chance.MAX)
    }

    @Test
    fun _60LatitudeEvaluatesToMediumChance() {
        val chance = impl.evaluate(GeomagLocation(60.0))

        assertThat(chance).isBetween(Chance(0.4), Chance(0.6))
    }

    @Test
    fun minus60LatitudeEvaluatesToMediumChance() {
        val chance = impl.evaluate(GeomagLocation(-60.0))

        assertThat(chance).isBetween(Chance(0.4), Chance(0.6))
    }
}
