package se.gustavkarlsson.aurora_notifier.android.background.providers;

import se.gustavkarlsson.aurora_notifier.android.models.factors.GeomagLocation;

public interface GeomagLocationProvider {
	GeomagLocation getGeomagLocation(double latitude, double longitude);
}
