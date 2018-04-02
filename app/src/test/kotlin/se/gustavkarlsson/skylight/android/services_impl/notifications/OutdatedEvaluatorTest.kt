package se.gustavkarlsson.skylight.android.services_impl.notifications

import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
import org.amshove.kluent.should
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.temporal.ChronoUnit.DAYS
import org.threeten.bp.temporal.ChronoUnit.HOURS
import se.gustavkarlsson.skylight.android.services.providers.TimeProvider

@RunWith(MockitoJUnitRunner::class)
class OutdatedEvaluatorTest {

    @Mock
	lateinit var mockTimeProvider: TimeProvider

    lateinit var impl: OutdatedEvaluator

    @Before
    fun setUp() {
        whenever(mockTimeProvider.getZoneId()).thenReturn(Single.just(ZONE_OFFSET))
        impl = OutdatedEvaluator(mockTimeProvider)
    }

    @Test
    fun testMultiple() {
        assertOutdated(BEFORE_MIDNIGHT, AFTER_MIDNIGHT, false)
        assertOutdated(BEFORE_MIDNIGHT, MIDNIGHT, false)
        assertOutdated(MIDNIGHT, AFTER_MIDNIGHT, false)

        assertOutdated(BEFORE_MIDNIGHT, BEFORE_NOON, false)
        assertOutdated(AFTER_MIDNIGHT, BEFORE_NOON, false)

        assertOutdated(BEFORE_MIDNIGHT, AFTER_NOON, true)
        assertOutdated(AFTER_MIDNIGHT, AFTER_NOON, true)

        assertOutdated(BEFORE_NOON, AFTER_NOON, true)

        assertOutdated(BEFORE_NOON.minus(1, DAYS), BEFORE_NOON, true)
        assertOutdated(BEFORE_NOON.minus(1, DAYS), AFTER_NOON, true)

        assertOutdated(AFTER_NOON.minus(1, DAYS), BEFORE_NOON, true)
        assertOutdated(AFTER_NOON.minus(1, DAYS), AFTER_NOON, true)
    }

    private fun assertOutdated(time: Instant, currentTime: Instant, expected: Boolean) {
		whenever(mockTimeProvider.getTime()).thenReturn(Single.just(currentTime))
		whenever(mockTimeProvider.getLocalDate()).thenReturn(
			Single.just(LocalDateTime.ofInstant(currentTime, ZONE_OFFSET).toLocalDate())
		)

        val outdated = impl.isOutdated(time)

		if (expected) {
			outdated.should("$time is not outdated but should be for current time: $currentTime") {
				outdated
			}
		} else {
			outdated.should("$time is outdated but should not be for current time: $currentTime") {
				!outdated
			}
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
