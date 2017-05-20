package se.gustavkarlsson.aurora_notifier.android.background.providers;

import se.gustavkarlsson.aurora_notifier.android.models.factors.Darkness;

public interface DarknessProvider {
	Darkness getDarkness(long timeMillis, double latitude, double longitude);
}
