package se.gustavkarlsson.skylight.android.services_impl.evaluation

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import org.amshove.kluent.shouldEqual
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import se.gustavkarlsson.skylight.android.entities.*
import se.gustavkarlsson.skylight.android.entities.Chance.Companion.IMPOSSIBLE
import se.gustavkarlsson.skylight.android.entities.Chance.Companion.UNKNOWN
import se.gustavkarlsson.skylight.android.mockito.any
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.test.shouldBeInRange

@RunWith(MockitoJUnitRunner::class)
class AuroraReportEvaluatorTest {
	@Mock
    lateinit var mockKpIndexEvaluator: ChanceEvaluator<KpIndex>

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
		whenever(mockKpIndexEvaluator.evaluate(any())).thenReturn(Chance(0.5))
        whenever(mockGeomagLocationEvaluator.evaluate(any())).thenReturn(Chance(0.5))
        whenever(mockVisibilityEvaluator.evaluate(any())).thenReturn(Chance(0.5))
        whenever(mockDarknessEvaluator.evaluate(any())).thenReturn(Chance(0.5))
		whenever(mockAuroraReport.factors).thenReturn(mock())
        impl = AuroraReportEvaluator(mockKpIndexEvaluator, mockGeomagLocationEvaluator, mockVisibilityEvaluator, mockDarknessEvaluator)
    }

    @Test
    fun oneImpossibleEvaluatesToImpossible() {
        whenever(mockGeomagLocationEvaluator.evaluate(any())).thenReturn(IMPOSSIBLE)

        val chance = impl.evaluate(mockAuroraReport)

		chance shouldEqual IMPOSSIBLE
    }

    @Test
    fun oneUnknownEvaluatesToUnknown() {
        whenever(mockGeomagLocationEvaluator.evaluate(any())).thenReturn(UNKNOWN)

        val chance = impl.evaluate(mockAuroraReport)

		chance shouldEqual UNKNOWN
    }

    @Test
    fun lowVisibilityEvaluatesToInvisibilityChance() {
        val lowChance = Chance(0.1)
        whenever(mockVisibilityEvaluator.evaluate(any())).thenReturn(lowChance)

        val chance = impl.evaluate(mockAuroraReport)

		chance shouldEqual lowChance
    }

    @Test
    fun lowActivityAndLocationEvaluatesToSomethingLower() {
        val lowestChance = Chance(0.2)
        val lowChance = Chance(0.4)
        whenever(mockKpIndexEvaluator.evaluate(any())).thenReturn(lowestChance)
        whenever(mockGeomagLocationEvaluator.evaluate(any())).thenReturn(lowChance)

        val chance = impl.evaluate(mockAuroraReport)

		chance shouldBeInRange IMPOSSIBLE..lowestChance
    }
}
