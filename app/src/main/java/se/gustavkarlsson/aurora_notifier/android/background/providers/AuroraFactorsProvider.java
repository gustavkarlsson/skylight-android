package se.gustavkarlsson.aurora_notifier.android.background.providers;

import android.location.Location;

import se.gustavkarlsson.aurora_notifier.android.models.AuroraFactors;

public interface AuroraFactorsProvider {
	AuroraFactors getAuroraFactors(Location location, long timeoutMillis);
}
