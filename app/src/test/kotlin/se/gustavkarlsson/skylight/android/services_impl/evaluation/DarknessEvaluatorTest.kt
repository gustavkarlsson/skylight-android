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
import se.gustavkarlsson.skylight.android.entities.Darkness

class DarknessEvaluatorTest {

	lateinit var impl: DarknessEvaluator

	@Before
	fun setUp() {
		impl = DarknessEvaluator()
	}

	@Test
	fun nullZenithAngleEvaluatesToUnknown() {
		val chance = impl.evaluate(Darkness(null))

		assert(chance).isEqualTo(UNKNOWN)
	}

	@Test
	fun _0ZenithAngleEvaluatesToImpossible() {
		val chance = impl.evaluate(Darkness(0.0))

		assert(chance).isEqualTo(IMPOSSIBLE)
	}

	@Test
	fun _90ZenithAngleEvaluatesToImpossible() {
		val chance = impl.evaluate(Darkness(90.0))

		assert(chance).isEqualTo(IMPOSSIBLE)
	}

	@Test
	fun _180ZenithAngleEvaluatesToMax() {
		val chance = impl.evaluate(Darkness(180.0))

		assert(chance).isEqualTo(MAX)
	}

	@Test
	fun minus180ZenithAngleEvaluatesToMax() {
		val chance = impl.evaluate(Darkness(-180.0))

		assert(chance).isEqualTo(MAX)
	}

	@Test
	fun minus360ZenithAngleEvaluatesToMax() {
		val chance = impl.evaluate(Darkness(-360.0))

		assert(chance).isEqualTo(MAX)
	}

	@Test
	fun _100ZenithAngleEvaluatesToMediumChance() {
		val chance = impl.evaluate(Darkness(100.0))

		assert(chance).isBetween(Chance(0.4), Chance(0.6))
	}
}
