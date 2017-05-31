package se.gustavkarlsson.skylight.android.background.providers;

import se.gustavkarlsson.skylight.android.models.factors.Darkness;

public interface DarknessProvider {
	Darkness getDarkness(long timeMillis, double latitude, double longitude);
}
