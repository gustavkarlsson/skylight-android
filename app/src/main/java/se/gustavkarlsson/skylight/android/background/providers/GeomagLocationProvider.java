package se.gustavkarlsson.skylight.android.background.providers;

import se.gustavkarlsson.skylight.android.models.factors.GeomagLocation;

public interface GeomagLocationProvider {
	GeomagLocation getGeomagLocation(double latitude, double longitude);
}
