package se.gustavkarlsson.skylight.android.lib.weather

import assertk.assertThat
import assertk.assertions.isBetween
import assertk.assertions.isEqualTo
import org.junit.Test
import se.gustavkarlsson.skylight.android.core.entities.Chance
import se.gustavkarlsson.skylight.android.core.entities.Chance.Companion.IMPOSSIBLE
import se.gustavkarlsson.skylight.android.core.entities.Chance.Companion.MAX
import se.gustavkarlsson.skylight.android.core.entities.Chance.Companion.UNKNOWN
import kotlin.time.Instant

class WeatherEvaluatorTest {

    private val timestamp = Instant.fromEpochMilliseconds(0)
    private val impl = WeatherEvaluator

    @Test
    fun _0CloudPercentageEvaluatesToMax() {
        val chance = impl.evaluate(Weather(0, timestamp))

        assertThat(chance).isEqualTo(MAX)
    }

    @Test
    fun _100CloudPercentageEvaluatesToImpossible() {
        val chance = impl.evaluate(Weather(100, timestamp))

        assertThat(chance).isEqualTo(IMPOSSIBLE)
    }

    @Test
    fun minus1CloudPercentageEvaluatesToUnknown() {
        val chance = impl.evaluate(Weather(-1, timestamp))

        assertThat(chance).isEqualTo(UNKNOWN)
    }

    @Test
    fun _101CloudPercentageEvaluatesToUnknown() {
        val chance = impl.evaluate(Weather(101, timestamp))

        assertThat(chance).isEqualTo(UNKNOWN)
    }

    @Test
    fun _50CloudPercentageEvaluatesToMediumChance() {
        val chance = impl.evaluate(Weather(25, timestamp))

        assertThat(chance).isBetween(Chance(0.4), Chance(0.6))
    }
}
