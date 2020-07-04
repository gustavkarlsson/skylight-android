package se.gustavkarlsson.skylight.android.lib.geomaglocation

import assertk.assert
import assertk.assertions.isBetween
import assertk.assertions.isEqualTo
import org.junit.Test
import se.gustavkarlsson.skylight.android.entities.Chance
import se.gustavkarlsson.skylight.android.entities.Chance.Companion.IMPOSSIBLE
import se.gustavkarlsson.skylight.android.entities.Chance.Companion.MAX
import se.gustavkarlsson.skylight.android.lib.geomaglocation.GeomagLocationEvaluator.BEST

class GeomagLocationEvaluatorTest {

    private val impl = GeomagLocationEvaluator

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
    fun bestLatitudeEvaluatesToMax() {
        val chance = impl.evaluate(GeomagLocation(BEST))

        assert(chance).isEqualTo(MAX)
    }

    @Test
    fun minusBestLatitudeEvaluatesToMax() {
        val chance = impl.evaluate(GeomagLocation(-BEST))

        assert(chance).isEqualTo(MAX)
    }

    @Test
    fun _60LatitudeEvaluatesToMediumChance() {
        val chance = impl.evaluate(GeomagLocation(60.0))

        assert(chance).isBetween(Chance(0.4), Chance(0.6))
    }

    @Test
    fun minus60LatitudeEvaluatesToMediumChance() {
        val chance = impl.evaluate(GeomagLocation(-60.0))

        assert(chance).isBetween(Chance(0.4), Chance(0.6))
    }
}
