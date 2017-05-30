package se.gustavkarlsson.aurora_notifier.android.util;

import org.threeten.bp.Clock;

public class CountdownTimer {
	private final long expiryTimeMillis;
	private final Clock clock;

	private CountdownTimer(long expiryTimeMillis, Clock clock) {
		this.expiryTimeMillis = clock.millis() + expiryTimeMillis;
		this.clock = clock;
	}

	public static CountdownTimer start(long durationMillis) {
		return start(durationMillis, Clock.systemUTC());
	}

	public static CountdownTimer start(long durationMillis, Clock clock) {
		return new CountdownTimer(durationMillis, clock);
	}

	public long getRemainingTimeMillis() {
		long remainingTIme = expiryTimeMillis - clock.millis();
		return Math.max(remainingTIme, 0);
	}

}
