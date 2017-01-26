package se.gustavkarlsson.aurora_notifier.android.background.update_tasks;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.background.ValueOrError;
import se.gustavkarlsson.aurora_notifier.android.background.providers.ProviderException;
import se.gustavkarlsson.aurora_notifier.android.background.providers.SunPositionProvider;
import se.gustavkarlsson.aurora_notifier.android.models.factors.SunPosition;

import static se.gustavkarlsson.aurora_notifier.android.background.ValueOrError.error;
import static se.gustavkarlsson.aurora_notifier.android.background.ValueOrError.value;

public class UpdateSunPositionTask extends AsyncTask<Object, Void, ValueOrError<SunPosition>> {
	private static final String TAG = UpdateSunPositionTask.class.getSimpleName();

	private final SunPositionProvider provider;
	private final Location location;
	private final long timeMillis;

	public UpdateSunPositionTask(SunPositionProvider provider, Location location, long timeMillis) {
		this.provider = provider;
		this.location = location;
		this.timeMillis = timeMillis;
	}

	@Override
	protected ValueOrError<SunPosition> doInBackground(Object... params) {
		try {
			Log.i(TAG, "Getting sun position...");
			SunPosition sunPosition = provider.getSunPosition(timeMillis, location.getLatitude(), location.getLongitude());
			Log.d(TAG, "Sun position is: " + sunPosition);
			if (sunPosition == null) {
				return error(R.string.could_not_determine_sun_position);
			}
			return value(sunPosition);
		} catch (ProviderException e) {
			return error(R.string.could_not_determine_sun_position);
		}
	}
}
