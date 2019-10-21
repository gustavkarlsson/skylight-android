package se.gustavkarlsson.skylight.android.services_impl.evaluation

import assertk.assert
import com.nhaarman.mockito_kotlin.whenever
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.threeten.bp.Instant
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.entities.Chance
import se.gustavkarlsson.skylight.android.entities.Chance.Companion.IMPOSSIBLE
import se.gustavkarlsson.skylight.android.entities.Chance.Companion.UNKNOWN
import se.gustavkarlsson.skylight.android.entities.Darkness
import se.gustavkarlsson.skylight.android.entities.GeomagLocation
import se.gustavkarlsson.skylight.android.entities.KpIndex
import se.gustavkarlsson.skylight.android.entities.Report
import se.gustavkarlsson.skylight.android.entities.Weather
import se.gustavkarlsson.skylight.android.mockito.any
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator

@RunWith(MockitoJUnitRunner::class)
class CompleteAuroraReportEvaluatorTest {
	@Mock
	lateinit var mockKpIndexEvaluator: ChanceEvaluator<KpIndex>

	@Mock
	lateinit var mockGeomagLocationEvaluator: ChanceEvaluator<GeomagLocation>

	@Mock
	lateinit var mockWeatherEvaluator: ChanceEvaluator<Weather>

	@Mock
	lateinit var mockDarknessEvaluator: ChanceEvaluator<Darkness>

	@Mock
	lateinit var mockAuroraReport: AuroraReport

	lateinit var impl: AuroraReportEvaluator

	@Before
	fun setUp() {
		whenever(mockAuroraReport.kpIndex).thenReturn(Report.success(KpIndex(7.0), Instant.EPOCH))
		whenever(mockAuroraReport.weather).thenReturn(Report.success(Weather(50), Instant.EPOCH))
		whenever(mockAuroraReport.geomagLocation).thenReturn(Report.success(GeomagLocation(50.0), Instant.EPOCH))
		whenever(mockAuroraReport.darkness).thenReturn(Report.success(Darkness(140.0), Instant.EPOCH))
		whenever(mockKpIndexEvaluator.evaluate(any())).thenReturn(Chance(0.5))
		whenever(mockGeomagLocationEvaluator.evaluate(any())).thenReturn(Chance(0.5))
		whenever(mockWeatherEvaluator.evaluate(any())).thenReturn(Chance(0.5))
		whenever(mockDarknessEvaluator.evaluate(any())).thenReturn(Chance(0.5))
		impl = AuroraReportEvaluator(
			mockKpIndexEvaluator,
			mockGeomagLocationEvaluator,
			mockWeatherEvaluator,
			mockDarknessEvaluator
		)
	}

	@Test
	fun oneImpossibleEvaluatesToImpossible() {
		whenever(mockGeomagLocationEvaluator.evaluate(any())).thenReturn(IMPOSSIBLE)

		val chance = impl.evaluate(mockAuroraReport)

		assert(chance).isEqualTo(IMPOSSIBLE)
	}

	@Test
	fun oneUnknownEvaluatesToUnknown() {
		whenever(mockGeomagLocationEvaluator.evaluate(any())).thenReturn(UNKNOWN)

		val chance = impl.evaluate(mockAuroraReport)

		assert(chance).isEqualTo(UNKNOWN)
	}

	@Test
	fun lowWeatherEvaluatesToLowChance() {
		val lowChance = Chance(0.1)
		whenever(mockWeatherEvaluator.evaluate(any())).thenReturn(lowChance)

		val chance = impl.evaluate(mockAuroraReport)

		assert(chance).isEqualTo(lowChance)
	}

	@Test
	fun lowActivityAndLocationEvaluatesToSomethingLower() {
		val lowestChance = Chance(0.2)
		val lowChance = Chance(0.4)
		whenever(mockKpIndexEvaluator.evaluate(any())).thenReturn(lowestChance)
		whenever(mockGeomagLocationEvaluator.evaluate(any())).thenReturn(lowChance)

		val chance = impl.evaluate(mockAuroraReport)

		assert(chance).isBetween(IMPOSSIBLE, lowestChance)
	}
}
