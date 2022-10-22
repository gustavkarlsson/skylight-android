package se.gustavkarlsson.skylight.android.feature.background.notifications

import assertk.assert
import assertk.assertions.isEqualTo
import assertk.tableOf
import com.nhaarman.mockito_kotlin.whenever
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import se.gustavkarlsson.skylight.android.lib.time.Time
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

@RunWith(MockitoJUnitRunner::class)
internal class OutdatedEvaluatorTest {

    @Mock
    lateinit var mockTime: Time

    lateinit var impl: OutdatedEvaluator

    @Before
    fun setUp() {
        whenever(mockTime.timeZone()).thenReturn(ZONE_OFFSET)
        impl = OutdatedEvaluator(
            mockTime,
        )
    }

    @Test
    fun testMultiple() {
        tableOf("time", "now", "expected")
            .row(BEFORE_MIDNIGHT, AFTER_MIDNIGHT, false)
            .row(BEFORE_MIDNIGHT, MIDNIGHT, false)
            .row(MIDNIGHT, AFTER_MIDNIGHT, false)
            .row(BEFORE_MIDNIGHT, BEFORE_NOON, false)
            .row(AFTER_MIDNIGHT, BEFORE_NOON, false)
            .row(BEFORE_MIDNIGHT, AFTER_NOON, true)
            .row(AFTER_MIDNIGHT, AFTER_NOON, true)
            .row(BEFORE_NOON, AFTER_NOON, true)
            .row(BEFORE_NOON - 1.days, BEFORE_NOON, true)
            .row(BEFORE_NOON - 1.days, AFTER_NOON, true)
            .row(AFTER_NOON - 1.days, BEFORE_NOON, true)
            .row(AFTER_NOON - 1.days, AFTER_NOON, true)
            .forAll { time, now, expected ->
                whenever(mockTime.now()).thenReturn(now)

                val actual = impl.isOutdated(time)

                assert(actual).isEqualTo(expected)
            }
    }

    companion object {
        private val ZONE_OFFSET = TimeZone.UTC
        private val MIDNIGHT = Instant.fromEpochMilliseconds(0)
        private val BEFORE_MIDNIGHT = MIDNIGHT - 1.seconds
        private val AFTER_MIDNIGHT = MIDNIGHT + 1.seconds
        private val NOON = MIDNIGHT + 12.hours
        private val AFTER_NOON = NOON + 1.seconds
        private val BEFORE_NOON = NOON - 1.seconds
    }
}
