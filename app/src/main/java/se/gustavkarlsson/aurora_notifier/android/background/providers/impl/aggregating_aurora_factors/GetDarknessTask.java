package se.gustavkarlsson.aurora_notifier.android.background.providers.impl.aggregating_aurora_factors;

import android.location.Location;
import android.util.Log;

import java.util.concurrent.FutureTask;

import se.gustavkarlsson.aurora_notifier.android.background.providers.DarknessProvider;
import se.gustavkarlsson.aurora_notifier.android.models.factors.Darkness;

class GetDarknessTask extends FutureTask<Darkness> {
	private static final String TAG = GetDarknessTask.class.getSimpleName();

	GetDarknessTask(DarknessProvider provider, Location location, long timeMillis) {
		super(() -> call(provider, location, timeMillis));
	}

	private static Darkness call(DarknessProvider provider, Location location, long timeMillis) {
		Log.i(TAG, "Getting darkness...");
		Darkness darkness = provider.getDarkness(timeMillis, location.getLatitude(), location.getLongitude());
		Log.d(TAG, "Darkness is: " + darkness);
		return darkness;
	}
}
