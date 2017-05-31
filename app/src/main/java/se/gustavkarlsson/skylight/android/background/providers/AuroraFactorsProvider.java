package se.gustavkarlsson.skylight.android.background.providers;

import android.location.Location;

import se.gustavkarlsson.skylight.android.models.AuroraFactors;

public interface AuroraFactorsProvider {
	AuroraFactors getAuroraFactors(Location location, long timeoutMillis);
}
