package se.gustavkarlsson.aurora_notifier.android.util;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;
import org.threeten.bp.Clock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CountdownTimerTest {

	@Mock
	Clock clock;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		when(clock.millis()).thenReturn(1000L);
	}

	@Test
	public void remainingIsSameAsInputValue() throws Exception {
		CountdownTimer timer = CountdownTimer.start(0, clock);

		long remainingMillis = timer.getRemainingTimeMillis();

		assertThat(remainingMillis).isEqualTo(0);
	}

	@Test
	public void remainingChangesWhenTimePasses() throws Exception {
		CountdownTimer timer = CountdownTimer.start(200, clock);

		when(clock.millis()).thenReturn(1100L);
		long remainingMillis = timer.getRemainingTimeMillis();

		assertThat(remainingMillis).isEqualTo(100);
	}

	@Test(expected = NullPointerException.class)
	public void nullClockThrowsNpe() throws Exception {
		CountdownTimer.start(200, null);
	}
}
