package se.gustavkarlsson.skylight.android.util;

import org.threeten.bp.Clock;
import org.threeten.bp.Duration;
import org.threeten.bp.Instant;

public class CountdownTimer {
	private final Instant expiryTime;
	private final Clock clock;

	public CountdownTimer(Duration duration, Clock clock) {
		this.expiryTime = clock.instant().plus(duration);
		this.clock = clock;
	}

	public Duration getRemainingTime() {
		Instant now = clock.instant();
		return Duration.between(now, expiryTime);
	}

}
