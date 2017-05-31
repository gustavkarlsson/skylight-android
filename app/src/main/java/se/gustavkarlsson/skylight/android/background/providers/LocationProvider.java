package se.gustavkarlsson.skylight.android.background.providers;

import android.location.Location;

public interface LocationProvider {
	Location getLocation(long timeoutMillis);
}
