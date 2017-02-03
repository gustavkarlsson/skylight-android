package se.gustavkarlsson.aurora_notifier.android.background.providers;

import se.gustavkarlsson.aurora_notifier.android.models.data.GeomagneticLocation;

public interface GeomagneticLocationProvider {
	GeomagneticLocation getGeomagneticLocation(double latitude, double longitude);
}
