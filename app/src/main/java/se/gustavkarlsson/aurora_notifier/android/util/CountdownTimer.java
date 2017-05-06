package se.gustavkarlsson.aurora_notifier.android.util;

public class CountdownTimer {
	private final long expiryTimeMillis;

	private CountdownTimer(long expiryTimeMillis) {
		this.expiryTimeMillis = expiryTimeMillis;
	}

	public static CountdownTimer start(long durationMillis) {
		return new CountdownTimer(System.currentTimeMillis() + durationMillis);
	}

	public long getRemainingTimeMillis() {
		long remainingTIme = expiryTimeMillis - System.currentTimeMillis();
		return Math.max(remainingTIme, 0);
	}

}
