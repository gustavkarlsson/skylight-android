package se.gustavkarlsson.aurora_notifier.android.background.tasks;

import android.os.AsyncTask;
import android.util.Log;

import se.gustavkarlsson.aurora_notifier.android.background.providers.SolarActivityProvider;
import se.gustavkarlsson.aurora_notifier.android.models.data.SolarActivity;

public class GetSolarActivityTask extends AsyncTask<Object, Void, SolarActivity> {
	private static final String TAG = GetSolarActivityTask.class.getSimpleName();

	private final SolarActivityProvider provider;

	public GetSolarActivityTask(SolarActivityProvider provider) {
		this.provider = provider;
	}

	@Override
	protected SolarActivity doInBackground(Object... params) {
		Log.i(TAG, "Getting solar activity...");
		SolarActivity solarActivity = provider.getSolarActivity();
		Log.d(TAG, "Solar activity is: " + solarActivity);
		return solarActivity;
	}
}
