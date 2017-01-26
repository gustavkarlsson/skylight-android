package se.gustavkarlsson.aurora_notifier.android.background.update_tasks;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.background.ValueOrError;
import se.gustavkarlsson.aurora_notifier.android.background.providers.ProviderException;
import se.gustavkarlsson.aurora_notifier.android.background.providers.WeatherProvider;
import se.gustavkarlsson.aurora_notifier.android.models.factors.Weather;

import static se.gustavkarlsson.aurora_notifier.android.background.ValueOrError.value;

public class UpdateWeatherTask extends AsyncTask<Object, Void, ValueOrError<Weather>> {
	private static final String TAG = UpdateSunPositionTask.class.getSimpleName();

	private final WeatherProvider provider;
	private final Location location;

	public UpdateWeatherTask(WeatherProvider provider, Location location) {
		this.provider = provider;
		this.location = location;
	}

	@Override
	protected ValueOrError<Weather> doInBackground(Object... params) {
		try {
			Log.i(TAG, "Getting weather...");
			Weather weather = provider.getWeather(location.getLatitude(), location.getLongitude());
			Log.d(TAG, "Weather is:  " + weather);
			if (weather == null) {
				return ValueOrError.error(R.string.could_not_determine_weather);
			}
			return value(weather);
		} catch (ProviderException e) {
			return ValueOrError.error(R.string.could_not_determine_weather);
		}
	}
}
