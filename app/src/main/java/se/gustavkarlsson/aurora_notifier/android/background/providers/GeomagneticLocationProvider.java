package se.gustavkarlsson.aurora_notifier.android.background.providers;

import se.gustavkarlsson.aurora_notifier.android.models.factors.GeomagneticLocation;

public interface GeomagneticLocationProvider {
	GeomagneticLocation getGeomagneticLocation(double latitude, double longitude);
}
