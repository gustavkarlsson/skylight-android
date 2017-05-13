package se.gustavkarlsson.aurora_notifier.android.background.tasks;

import android.location.Location;
import android.util.Log;

import java.util.concurrent.FutureTask;

import se.gustavkarlsson.aurora_notifier.android.background.providers.SunPositionProvider;
import se.gustavkarlsson.aurora_notifier.android.models.factors.SunPosition;

public class GetSunPositionTask extends FutureTask<SunPosition> {
	private static final String TAG = GetSunPositionTask.class.getSimpleName();

	public GetSunPositionTask(SunPositionProvider provider, Location location, long timeMillis) {
		super(() -> call(provider, location, timeMillis));
	}

	private static SunPosition call(SunPositionProvider provider, Location location, long timeMillis) {
		Log.i(TAG, "Getting sun position...");
		SunPosition sunPosition = provider.getSunPosition(timeMillis, location.getLatitude(), location.getLongitude());
		Log.d(TAG, "Sun position is: " + sunPosition);
		return sunPosition;
	}
}
