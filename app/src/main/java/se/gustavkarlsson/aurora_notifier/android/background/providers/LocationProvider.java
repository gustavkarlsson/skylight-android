package se.gustavkarlsson.aurora_notifier.android.background.providers;

import android.location.Location;

public interface LocationProvider {
	Location getLocation(long timeoutMillis);
}
