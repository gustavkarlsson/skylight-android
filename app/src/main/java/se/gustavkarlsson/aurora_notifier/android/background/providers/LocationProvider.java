package se.gustavkarlsson.aurora_notifier.android.background.providers;

import android.location.Location;

import se.gustavkarlsson.aurora_notifier.android.background.ValueOrError;

public interface LocationProvider {
	ValueOrError<Location> getLocation(long timeoutMillis);
}
