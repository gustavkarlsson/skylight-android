package se.gustavkarlsson.skylight.android.services_impl.notifications

import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.SoftAssertions
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.threeten.bp.Clock
import org.threeten.bp.Instant
import org.threeten.bp.ZoneOffset
import org.threeten.bp.temporal.ChronoUnit.DAYS
import org.threeten.bp.temporal.ChronoUnit.HOURS
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.providers.ZoneIdProvider

@RunWith(MockitoJUnitRunner::class)
class ReportOutdatedEvaluatorTest {

    @Mock
	lateinit var mockClock: Clock

    @Mock
	lateinit var mockZoneIdProvider: ZoneIdProvider

    @Mock
    lateinit var mockReport: AuroraReport

    lateinit var impl: ReportOutdatedEvaluator

    @Before
    fun setUp() {
        whenever(mockZoneIdProvider.zoneId).thenReturn(ZONE_OFFSET)
        whenever(mockClock.zone).thenReturn(ZONE_OFFSET)
        impl = ReportOutdatedEvaluator(mockClock, mockZoneIdProvider)
    }

    @Test
    fun testMultiple() {
        val softly = SoftAssertions()

        assertOutdated(BEFORE_MIDNIGHT, AFTER_MIDNIGHT, false, softly)
        assertOutdated(BEFORE_MIDNIGHT, MIDNIGHT, false, softly)
        assertOutdated(MIDNIGHT, AFTER_MIDNIGHT, false, softly)

        assertOutdated(BEFORE_MIDNIGHT, BEFORE_NOON, false, softly)
        assertOutdated(AFTER_MIDNIGHT, BEFORE_NOON, false, softly)

        assertOutdated(BEFORE_MIDNIGHT, AFTER_NOON, true, softly)
        assertOutdated(AFTER_MIDNIGHT, AFTER_NOON, true, softly)

        assertOutdated(BEFORE_NOON, AFTER_NOON, true, softly)

        assertOutdated(BEFORE_NOON.minus(1, DAYS), BEFORE_NOON, true, softly)
        assertOutdated(BEFORE_NOON.minus(1, DAYS), AFTER_NOON, true, softly)

        assertOutdated(AFTER_NOON.minus(1, DAYS), BEFORE_NOON, true, softly)
        assertOutdated(AFTER_NOON.minus(1, DAYS), AFTER_NOON, true, softly)

        softly.assertAll()
    }

    private fun assertOutdated(lastReportTime: Instant, currentTime: Instant, expected: Boolean, softly: SoftAssertions) {
        whenever(mockReport.timestamp).thenReturn(lastReportTime)
        whenever(mockClock.instant()).thenReturn(currentTime)

        val outdated = impl.isOutdated(mockReport)

        val assertion = softly.assertThat(outdated).`as`("Last report time: %s, Current time: %s", lastReportTime, currentTime)
        if (expected) {
            assertion.isTrue
        } else {
            assertion.isFalse
        }
    }

	companion object {
        private val ZONE_OFFSET = ZoneOffset.UTC
        private val MIDNIGHT = Instant.EPOCH
        private val BEFORE_MIDNIGHT = MIDNIGHT.minusSeconds(1)
        private val AFTER_MIDNIGHT = MIDNIGHT.plusSeconds(1)
        private val NOON = MIDNIGHT.plus(12, HOURS)
        private val AFTER_NOON = NOON.plusSeconds(1)
        private val BEFORE_NOON = NOON.minusSeconds(1)
	}

}
