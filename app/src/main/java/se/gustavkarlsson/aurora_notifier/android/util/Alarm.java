package se.gustavkarlsson.aurora_notifier.android.util;

public class Alarm {
	private final long expiryTimeMillis;

	private Alarm(long expiryTimeMillis) {
		this.expiryTimeMillis = expiryTimeMillis;
	}

	public static Alarm start(long durationMillis) {
		return new Alarm(System.currentTimeMillis() + durationMillis);
	}

	public long getRemainingTimeMillis() {
		long remainingTIme = expiryTimeMillis - System.currentTimeMillis();
		return Math.max(remainingTIme, 0);
	}

}
