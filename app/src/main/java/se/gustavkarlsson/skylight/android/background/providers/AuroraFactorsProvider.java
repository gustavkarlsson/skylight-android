package se.gustavkarlsson.skylight.android.background.providers;

import android.location.Location;

import org.threeten.bp.Duration;

import se.gustavkarlsson.skylight.android.models.AuroraFactors;

public interface AuroraFactorsProvider {
	AuroraFactors getAuroraFactors(Location location, Duration timeout);
}
