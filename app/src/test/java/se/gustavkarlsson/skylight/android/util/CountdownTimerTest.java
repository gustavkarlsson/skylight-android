package se.gustavkarlsson.skylight.android.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.threeten.bp.Clock;
import org.threeten.bp.Duration;
import org.threeten.bp.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CountdownTimerTest {

	@Mock
	Clock clock;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(clock.instant()).thenReturn(Instant.ofEpochMilli(1000));
	}

	@Test
	public void remainingIsSameAsInputValue() throws Exception {
		CountdownTimer timer = new CountdownTimer(Duration.ZERO, clock);

		Duration remainingTime = timer.getRemainingTime();

		assertThat(remainingTime).isEqualTo(Duration.ZERO);
	}

	@Test
	public void remainingChangesWhenTimePasses() throws Exception {
		CountdownTimer timer = new CountdownTimer(Duration.ofMillis(200), clock);

		when(clock.instant()).thenReturn(Instant.ofEpochMilli(1100));
		Duration remainingTime = timer.getRemainingTime();

		assertThat(remainingTime).isEqualTo(Duration.ofMillis(100));
	}

	@Test(expected = NullPointerException.class)
	public void nullClockThrowsNpe() throws Exception {
		new CountdownTimer(Duration.ofMillis(200), null);
	}
}
