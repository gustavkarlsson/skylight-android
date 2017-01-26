package se.gustavkarlsson.aurora_notifier.android.background.update_tasks;

import android.os.AsyncTask;
import android.util.Log;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.background.ValueOrError;
import se.gustavkarlsson.aurora_notifier.android.background.providers.SolarActivityProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.ProviderException;
import se.gustavkarlsson.aurora_notifier.android.models.factors.SolarActivity;

import static se.gustavkarlsson.aurora_notifier.android.background.ValueOrError.error;
import static se.gustavkarlsson.aurora_notifier.android.background.ValueOrError.value;

public class UpdateSolarActivityTask extends AsyncTask<Object, Void, ValueOrError<SolarActivity>> {
	private static final String TAG = UpdateSolarActivityTask.class.getSimpleName();

	private final SolarActivityProvider provider;

	public UpdateSolarActivityTask(SolarActivityProvider provider) {
		this.provider = provider;
	}

	@Override
	protected ValueOrError<SolarActivity> doInBackground(Object... params) {
		try {
			Log.i(TAG, "Getting KP index...");
			SolarActivity solarActivity = provider.getSolarActivity();
			Log.d(TAG, "Solar activity is: " + solarActivity);
			if (solarActivity == null) {
				return error(R.string.could_not_determine_solar_activity);
			}
			return value(solarActivity);
		} catch (ProviderException e) {
			return error(R.string.could_not_determine_solar_activity);
		}
	}
}
