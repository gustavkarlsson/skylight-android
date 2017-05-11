package se.gustavkarlsson.aurora_notifier.android.background.tasks;

import android.location.Location;
import android.util.Log;

import java.util.concurrent.FutureTask;

import se.gustavkarlsson.aurora_notifier.android.background.providers.WeatherProvider;
import se.gustavkarlsson.aurora_notifier.android.models.data.Weather;

public class GetWeatherTask extends FutureTask<Weather> {
	private static final String TAG = GetWeatherTask.class.getSimpleName();

	public GetWeatherTask(WeatherProvider provider, Location location) {
		super(() -> call(provider, location));
	}

	private static Weather call(WeatherProvider provider, Location location) throws Exception {
		Log.i(TAG, "Getting weather...");
		Weather weather = provider.getWeather(location.getLatitude(), location.getLongitude());
		Log.d(TAG, "Weather is:  " + weather);
		return weather;
	}
}
