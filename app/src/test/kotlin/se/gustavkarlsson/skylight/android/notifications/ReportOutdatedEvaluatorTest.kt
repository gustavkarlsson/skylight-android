package se.gustavkarlsson.skylight.android.notifications

import org.assertj.core.api.SoftAssertions
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnit
import org.threeten.bp.Clock
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.ZoneOffset
import org.threeten.bp.temporal.ChronoUnit.DAYS
import org.threeten.bp.temporal.ChronoUnit.HOURS
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.util.ZoneIdProvider

private val ZONE_OFFSET = ZoneOffset.UTC
private val MIDNIGHT = Instant.EPOCH
private val BEFORE_MIDNIGHT = MIDNIGHT.minusSeconds(1)
private val AFTER_MIDNIGHT = MIDNIGHT.plusSeconds(1)
private val NOON = MIDNIGHT.plus(12, HOURS)
private val AFTER_NOON = NOON.plusSeconds(1)
private val BEFORE_NOON = NOON.minusSeconds(1)

class ReportOutdatedEvaluatorTest {

	@Rule
	@JvmField
	val rule = MockitoJUnit.rule()!!

    @Mock
	lateinit var clock: Clock

    @Mock
	lateinit var zoneIdProvider: ZoneIdProvider

    @Mock
    lateinit var report: AuroraReport

    lateinit var evaluator: ReportOutdatedEvaluator

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        `when`<ZoneId>(zoneIdProvider.zoneId).thenReturn(ZONE_OFFSET)
        `when`<ZoneId>(clock.zone).thenReturn(ZONE_OFFSET)
        evaluator = ReportOutdatedEvaluator(clock, zoneIdProvider)
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
        `when`(report.timestamp).thenReturn(lastReportTime)
        `when`(clock.instant()).thenReturn(currentTime)
        `when`(clock.millis()).thenReturn(currentTime.toEpochMilli())

        val outdated = evaluator.isOutdated(report)

        val assertion = softly.assertThat(outdated).`as`("Last report time: %s, Current time: %s", lastReportTime, currentTime)
        if (expected) {
            assertion.isTrue
        } else {
            assertion.isFalse
        }
    }

}
