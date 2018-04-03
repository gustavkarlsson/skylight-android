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
import se.gustavkarlsson.skylight.android.entities.GeomagLocation

class GeomagLocationEvaluatorTest {

    lateinit var impl: GeomagLocationEvaluator

    @Before
    fun setUp() {
        impl = GeomagLocationEvaluator()
    }

    @Test
    fun nullLatitudeEvaluatesToUnknown() {
        val chance = impl.evaluate(GeomagLocation(null))

        assert(chance).isEqualTo(UNKNOWN)
    }

    @Test
    fun _0LatitudeEvaluatesToImpossible() {
        val chance = impl.evaluate(GeomagLocation(0.0))

        assert(chance).isEqualTo(IMPOSSIBLE)
    }

    @Test
    fun _90LatitudeEvaluatesToImpossible() {
        val chance = impl.evaluate(GeomagLocation(90.0))

        assert(chance).isEqualTo(IMPOSSIBLE)
    }

    @Test
    fun minus90LatitudeEvaluatesToImpossible() {
        val chance = impl.evaluate(GeomagLocation(-90.0))

        assert(chance).isEqualTo(IMPOSSIBLE)
    }

    @Test
    fun _67LatitudeEvaluatesToMax() {
        val chance = impl.evaluate(GeomagLocation(67.0))

        assert(chance).isEqualTo(MAX)
    }

    @Test
    fun minus67LatitudeEvaluatesToMax() {
        val chance = impl.evaluate(GeomagLocation(-67.0))

        assert(chance).isEqualTo(MAX)
    }

    @Test
    fun _60LatitudeEvaluatesToMediumChance() {
        val chance = impl.evaluate(GeomagLocation(60.0))

		assertk.assert(chance).isBetween(Chance(0.4), Chance(0.6))
    }

    @Test
    fun minus60LatitudeEvaluatesToMediumChance() {
        val chance = impl.evaluate(GeomagLocation(-60.0))

		assertk.assert(chance).isBetween(Chance(0.4), Chance(0.6))
    }
}
