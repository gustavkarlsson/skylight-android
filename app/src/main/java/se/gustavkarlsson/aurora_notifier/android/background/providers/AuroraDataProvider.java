package se.gustavkarlsson.aurora_notifier.android.background.providers;

import android.location.Location;

import se.gustavkarlsson.aurora_notifier.android.models.AuroraData;

public interface AuroraDataProvider {
	AuroraData getAuroraData(long timeoutMillis, Location location);
}
