package se.gustavkarlsson.skylight.android.services_impl.evaluation

import org.amshove.kluent.shouldEqual
import org.junit.Before
import org.junit.Test
import se.gustavkarlsson.skylight.android.entities.Chance
import se.gustavkarlsson.skylight.android.entities.Chance.Companion.IMPOSSIBLE
import se.gustavkarlsson.skylight.android.entities.Chance.Companion.MAX
import se.gustavkarlsson.skylight.android.entities.Chance.Companion.UNKNOWN
import se.gustavkarlsson.skylight.android.entities.GeomagLocation
import se.gustavkarlsson.skylight.android.test.shouldBeInRange

class GeomagLocationEvaluatorTest {

    lateinit var impl: GeomagLocationEvaluator

    @Before
    fun setUp() {
        impl = GeomagLocationEvaluator()
    }

    @Test
    fun nullLatitudeEvaluatesToUnknown() {
        val chance = impl.evaluate(GeomagLocation(null))

        chance shouldEqual UNKNOWN
    }

    @Test
    fun _0LatitudeEvaluatesToImpossible() {
        val chance = impl.evaluate(GeomagLocation(0.0))

        chance shouldEqual IMPOSSIBLE
    }

    @Test
    fun _90LatitudeEvaluatesToImpossible() {
        val chance = impl.evaluate(GeomagLocation(90.0))

        chance shouldEqual IMPOSSIBLE
    }

    @Test
    fun minus90LatitudeEvaluatesToImpossible() {
        val chance = impl.evaluate(GeomagLocation(-90.0))

        chance shouldEqual IMPOSSIBLE
    }

    @Test
    fun _67LatitudeEvaluatesToMax() {
        val chance = impl.evaluate(GeomagLocation(67.0))

        chance shouldEqual MAX
    }

    @Test
    fun minus67LatitudeEvaluatesToMax() {
        val chance = impl.evaluate(GeomagLocation(-67.0))

        chance shouldEqual MAX
    }

    @Test
    fun _60LatitudeEvaluatesToMediumChance() {
        val chance = impl.evaluate(GeomagLocation(60.0))

		chance shouldBeInRange Chance(0.4)..Chance(0.6)
    }

    @Test
    fun minus60LatitudeEvaluatesToMediumChance() {
        val chance = impl.evaluate(GeomagLocation(-60.0))

		chance shouldBeInRange Chance(0.4)..Chance(0.6)
    }
}
