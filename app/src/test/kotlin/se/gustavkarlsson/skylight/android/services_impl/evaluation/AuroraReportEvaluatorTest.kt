package se.gustavkarlsson.skylight.android.services_impl.evaluation

import com.nhaarman.mockito_kotlin.mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import se.gustavkarlsson.skylight.android.entities.*
import se.gustavkarlsson.skylight.android.mockito.any
import se.gustavkarlsson.skylight.android.services.evaluation.Chance
import se.gustavkarlsson.skylight.android.services.evaluation.Chance.Companion.IMPOSSIBLE
import se.gustavkarlsson.skylight.android.services.evaluation.Chance.Companion.UNKNOWN
import se.gustavkarlsson.skylight.android.services.evaluation.ChanceEvaluator

@RunWith(MockitoJUnitRunner::class)
class AuroraReportEvaluatorTest {
	@Mock
    lateinit var mockGeomagActivityEvaluator: ChanceEvaluator<GeomagActivity>

    @Mock
    lateinit var mockGeomagLocationEvaluator: ChanceEvaluator<GeomagLocation>

    @Mock
    lateinit var mockVisibilityEvaluator: ChanceEvaluator<Visibility>

    @Mock
    lateinit var mockDarknessEvaluator: ChanceEvaluator<Darkness>

    @Mock
    lateinit var mockAuroraReport: AuroraReport

	lateinit var impl: AuroraReportEvaluator

    @Before
    fun setUp() {
		`when`(mockGeomagActivityEvaluator.evaluate(any())).thenReturn(Chance(0.5))
        `when`(mockGeomagLocationEvaluator.evaluate(any())).thenReturn(Chance(0.5))
        `when`(mockVisibilityEvaluator.evaluate(any())).thenReturn(Chance(0.5))
        `when`(mockDarknessEvaluator.evaluate(any())).thenReturn(Chance(0.5))
		`when`(mockAuroraReport.factors).thenReturn(mock())
        impl = AuroraReportEvaluator(mockGeomagActivityEvaluator, mockGeomagLocationEvaluator, mockVisibilityEvaluator, mockDarknessEvaluator)
    }

    @Test
    fun oneImpossibleEvaluatesToImpossible() {
        `when`(mockGeomagLocationEvaluator.evaluate(any())).thenReturn(IMPOSSIBLE)

        val chance = impl.evaluate(mockAuroraReport)

		assertThat(chance).isEqualTo(IMPOSSIBLE)
    }

    @Test
    fun oneUnknownEvaluatesToUnknown() {
        `when`(mockGeomagLocationEvaluator.evaluate(any())).thenReturn(UNKNOWN)

        val chance = impl.evaluate(mockAuroraReport)

        assertThat(chance).isEqualTo(UNKNOWN)
    }

    @Test
    fun lowVisibilityEvaluatesToInvisibilityChance() {
        val lowChance = Chance(0.1)
        `when`(mockVisibilityEvaluator.evaluate(any())).thenReturn(lowChance)

        val chance = impl.evaluate(mockAuroraReport)

        assertThat(chance).isEqualTo(lowChance)
    }

    @Test
    fun lowActivityAndLocationEvaluatesToSomethingLower() {
        val lowestChance = Chance(0.2)
        val lowChance = Chance(0.4)
        `when`(mockGeomagActivityEvaluator.evaluate(any())).thenReturn(lowestChance)
        `when`(mockGeomagLocationEvaluator.evaluate(any())).thenReturn(lowChance)

        val chance = impl.evaluate(mockAuroraReport)

        assertThat(chance).isBetween(IMPOSSIBLE, lowestChance)
    }
}
