package se.gustavkarlsson.aurora_notifier.android.background.update_tasks;

import android.os.AsyncTask;
import android.util.Log;

import se.gustavkarlsson.aurora_notifier.android.background.providers.SolarActivityProvider;
import se.gustavkarlsson.aurora_notifier.android.models.data.SolarActivity;


public class UpdateSolarActivityTask extends AsyncTask<Object, Void, SolarActivity> {
	private static final String TAG = UpdateSolarActivityTask.class.getSimpleName();

	private final SolarActivityProvider provider;

	public UpdateSolarActivityTask(SolarActivityProvider provider) {
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
