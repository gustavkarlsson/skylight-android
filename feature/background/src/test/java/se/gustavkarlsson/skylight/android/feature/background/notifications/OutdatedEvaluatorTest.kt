package se.gustavkarlsson.skylight.android.feature.background.notifications

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.tableOf
import kotlinx.datetime.TimeZone
import org.junit.Test
import se.gustavkarlsson.skylight.android.lib.time.Time
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

internal class OutdatedEvaluatorTest {

    private var now = Instant.DISTANT_PAST
    private var timeZone = TimeZone.UTC

    private val fakeTime = object : Time {
        override fun now(): Instant = now
        override fun timeZone(): TimeZone = timeZone
    }

    private val impl = OutdatedEvaluator(fakeTime)

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
                this.now = now

                val actual = impl.isOutdated(time)

                assertThat(actual).isEqualTo(expected)
            }
    }

    companion object {
        private val MIDNIGHT = Instant.fromEpochMilliseconds(0)
        private val BEFORE_MIDNIGHT = MIDNIGHT - 1.seconds
        private val AFTER_MIDNIGHT = MIDNIGHT + 1.seconds
        private val NOON = MIDNIGHT + 12.hours
        private val AFTER_NOON = NOON + 1.seconds
        private val BEFORE_NOON = NOON - 1.seconds
    }
}
