package se.gustavkarlsson.aurora_notifier.android.background.tasks;

import android.location.Location;
import android.util.Log;

import java.util.concurrent.FutureTask;

import se.gustavkarlsson.aurora_notifier.android.background.providers.DarknessProvider;
import se.gustavkarlsson.aurora_notifier.android.models.factors.Darkness;

public class GetDarknessTask extends FutureTask<Darkness> {
	private static final String TAG = GetDarknessTask.class.getSimpleName();

	public GetDarknessTask(DarknessProvider provider, Location location, long timeMillis) {
		super(() -> call(provider, location, timeMillis));
	}

	private static Darkness call(DarknessProvider provider, Location location, long timeMillis) {
		Log.i(TAG, "Getting darkness...");
		Darkness darkness = provider.getDarkness(timeMillis, location.getLatitude(), location.getLongitude());
		Log.d(TAG, "Darkness is: " + darkness);
		return darkness;
	}
}
