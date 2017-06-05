package se.gustavkarlsson.skylight.android.background.providers;

import org.threeten.bp.Instant;

import se.gustavkarlsson.skylight.android.models.factors.Darkness;

public interface DarknessProvider {
	Darkness getDarkness(Instant time, double latitude, double longitude);
}
