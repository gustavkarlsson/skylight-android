package se.gustavkarlsson.aurora_notifier.android.background.providers;

import android.location.Location;

import se.gustavkarlsson.aurora_notifier.android.models.AuroraData;

public interface AuroraDataProvider {
	AuroraData getAuroraData(Location location, long timeoutMillis);
}
