package se.gustavkarlsson.aurora_notifier.android.background.providers;

import se.gustavkarlsson.aurora_notifier.android.models.data.SunPosition;

public interface SunPositionProvider {
	SunPosition getSunPosition(long timeMillis, double latitude, double longitude);
}
