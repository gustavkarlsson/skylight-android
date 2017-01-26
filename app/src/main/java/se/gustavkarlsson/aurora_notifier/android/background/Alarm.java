package se.gustavkarlsson.aurora_notifier.android.background;

class Alarm {
	private final long expiryTimeMillis;

	private Alarm(long expiryTimeMillis) {
		this.expiryTimeMillis = expiryTimeMillis;
	}

	static Alarm start(long durationMillis) {
		return new Alarm(System.currentTimeMillis() + durationMillis);
	}

	long getRemainingTimeMillis() {
		long remainingTIme = expiryTimeMillis - System.currentTimeMillis();
		return Math.max(remainingTIme, 0);
	}

}
