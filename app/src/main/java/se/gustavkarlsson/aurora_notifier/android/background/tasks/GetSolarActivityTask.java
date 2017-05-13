package se.gustavkarlsson.aurora_notifier.android.background.tasks;

import android.util.Log;

import java.util.concurrent.FutureTask;

import se.gustavkarlsson.aurora_notifier.android.background.providers.SolarActivityProvider;
import se.gustavkarlsson.aurora_notifier.android.models.factors.SolarActivity;

public class GetSolarActivityTask extends FutureTask<SolarActivity> {
	private static final String TAG = GetSolarActivityTask.class.getSimpleName();

	public GetSolarActivityTask(SolarActivityProvider provider) {
		super(() -> call(provider));
	}

	private static SolarActivity call(SolarActivityProvider provider) {
		Log.i(TAG, "Getting solar activity...");
		SolarActivity solarActivity = provider.getSolarActivity();
		Log.d(TAG, "Solar activity is: " + solarActivity);
		return solarActivity;
	}
}
