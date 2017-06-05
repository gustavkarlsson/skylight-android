package se.gustavkarlsson.skylight.android.background.providers;

import android.location.Location;

import org.threeten.bp.Duration;

public interface LocationProvider {
	Location getLocation(Duration timeout);
}
