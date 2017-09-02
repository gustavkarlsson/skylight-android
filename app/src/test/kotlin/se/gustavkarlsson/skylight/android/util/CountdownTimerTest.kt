package se.gustavkarlsson.skylight.android.util

import com.nhaarman.mockito_kotlin.whenever
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnit
import org.threeten.bp.Clock
import org.threeten.bp.Duration
import org.threeten.bp.Instant

class CountdownTimerTest {

	@Rule
	@JvmField
	val rule = MockitoJUnit.rule()!!

	@Mock
	lateinit var clock: Clock

	@Before
	fun setUp() {
		MockitoAnnotations.initMocks(this)
		whenever(clock.instant()).thenReturn(Instant.ofEpochMilli(1000))
	}

	@Test
	fun remainingIsSameAsInputValue() {
		val timer = CountdownTimer(Duration.ZERO, clock)

		val remainingTime = timer.remainingTime

		assertThat(remainingTime).isEqualTo(Duration.ZERO)
	}

	@Test
	fun remainingChangesWhenTimePasses() {
		val timer = CountdownTimer(Duration.ofMillis(200), clock)
		whenever(clock.instant()).thenReturn(Instant.ofEpochMilli(1100))

		val remainingTime = timer.remainingTime

		assertThat(remainingTime).isEqualTo(Duration.ofMillis(100))
	}
}
