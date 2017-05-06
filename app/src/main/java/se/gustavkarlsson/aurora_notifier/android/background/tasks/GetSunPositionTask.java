package se.gustavkarlsson.aurora_notifier.android.background.tasks;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import se.gustavkarlsson.aurora_notifier.android.background.providers.SunPositionProvider;
import se.gustavkarlsson.aurora_notifier.android.models.data.SunPosition;

public class GetSunPositionTask extends AsyncTask<Object, Void, SunPosition> {
	private static final String TAG = GetSunPositionTask.class.getSimpleName();

	private final SunPositionProvider provider;
	private final Location location;
	private final long timeMillis;

	public GetSunPositionTask(SunPositionProvider provider, Location location, long timeMillis) {
		this.provider = provider;
		this.location = location;
		this.timeMillis = timeMillis;
	}

	@Override
	protected SunPosition doInBackground(Object... params) {
		Log.i(TAG, "Getting sun position...");
		SunPosition sunPosition = provider.getSunPosition(timeMillis, location.getLatitude(), location.getLongitude());
		Log.d(TAG, "Sun position is: " + sunPosition);
		return sunPosition;
	}
}
