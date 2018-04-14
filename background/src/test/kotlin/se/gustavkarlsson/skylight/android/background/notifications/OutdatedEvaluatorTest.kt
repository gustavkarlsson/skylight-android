package se.gustavkarlsson.skylight.android.background.notifications

import assertk.assert
import assertk.assertions.isEqualTo
import assertk.tableOf
import com.nhaarman.mockito_kotlin.whenever
import io.reactivex.Single
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
internal class OutdatedEvaluatorTest {

	@Mock
	lateinit var mockTimeProvider: TimeProvider

	lateinit var impl: OutdatedEvaluator

	@Before
	fun setUp() {
		whenever(mockTimeProvider.getZoneId()).thenReturn(Single.just(ZONE_OFFSET))
		impl = OutdatedEvaluator(
			mockTimeProvider
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
			.row(BEFORE_NOON.minus(1, DAYS), BEFORE_NOON, true)
			.row(BEFORE_NOON.minus(1, DAYS), AFTER_NOON, true)
			.row(AFTER_NOON.minus(1, DAYS), BEFORE_NOON, true)
			.row(AFTER_NOON.minus(1, DAYS), AFTER_NOON, true)
			.forAll { time, now, expected ->
				whenever(mockTimeProvider.getTime()).thenReturn(Single.just(now))
				whenever(mockTimeProvider.getLocalDate()).thenReturn(
					Single.just(LocalDateTime.ofInstant(now, ZONE_OFFSET).toLocalDate())
				)

				val actual = impl.isOutdated(time)

				assert(actual).isEqualTo(expected)
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
