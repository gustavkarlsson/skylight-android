package se.gustavkarlsson.aurora_notifier.android.background.update_tasks;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import se.gustavkarlsson.aurora_notifier.android.background.providers.WeatherProvider;
import se.gustavkarlsson.aurora_notifier.android.models.factors.Weather;


public class UpdateWeatherTask extends AsyncTask<Object, Void, Weather> {
	private static final String TAG = UpdateSunPositionTask.class.getSimpleName();

	private final WeatherProvider provider;
	private final Location location;

	public UpdateWeatherTask(WeatherProvider provider, Location location) {
		this.provider = provider;
		this.location = location;
	}

	@Override
	protected Weather doInBackground(Object... params) {
		Log.i(TAG, "Getting weather...");
		Weather weather = provider.getWeather(location.getLatitude(), location.getLongitude());
		Log.d(TAG, "Weather is:  " + weather);
		return weather;
	}
}
