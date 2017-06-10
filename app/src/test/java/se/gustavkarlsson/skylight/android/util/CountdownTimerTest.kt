package se.gustavkarlsson.skylight.android.util

import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.runners.MockitoJUnitRunner
import org.threeten.bp.Clock
import org.threeten.bp.Duration
import org.threeten.bp.Instant

@RunWith(MockitoJUnitRunner::class)
class CountdownTimerTest {

    @Mock
    lateinit var clock: Clock

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
        `when`(clock.instant()).thenReturn(Instant.ofEpochMilli(1000))
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
        `when`(clock.instant()).thenReturn(Instant.ofEpochMilli(1100))

        val remainingTime = timer.remainingTime

        assertThat(remainingTime).isEqualTo(Duration.ofMillis(100))
    }
}
