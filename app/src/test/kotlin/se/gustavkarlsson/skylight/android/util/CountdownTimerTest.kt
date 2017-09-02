package se.gustavkarlsson.skylight.android.util

import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.threeten.bp.Clock
import org.threeten.bp.Duration
import org.threeten.bp.Instant

@RunWith(MockitoJUnitRunner::class)
class CountdownTimerTest {

	@Mock
	lateinit var mockClock: Clock

	@Before
	fun setUp() {
		whenever(mockClock.instant()).thenReturn(Instant.ofEpochMilli(1000))
	}

	@Test
	fun remainingIsSameAsInputValue() {
		val timer = CountdownTimer(Duration.ZERO, mockClock)

		val remainingTime = timer.remainingTime

		assertThat(remainingTime).isEqualTo(Duration.ZERO)
	}

	@Test
	fun remainingChangesWhenTimePasses() {
		val timer = CountdownTimer(Duration.ofMillis(200), mockClock)
		whenever(mockClock.instant()).thenReturn(Instant.ofEpochMilli(1100))

		val remainingTime = timer.remainingTime

		assertThat(remainingTime).isEqualTo(Duration.ofMillis(100))
	}
}
