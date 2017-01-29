package se.gustavkarlsson.aurora_notifier.android.background.providers.impl;

import android.util.Log;

import java.io.IOException;

import retrofit2.Response;
import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.background.providers.WeatherProvider;
import se.gustavkarlsson.aurora_notifier.android.models.factors.Weather;
import se.gustavkarlsson.aurora_notifier.android.util.UserFriendlyException;

// TODO Look into using their json API
public class RetrofittedOpenWeatherMapProvider implements WeatherProvider {
	private static final String TAG = RetrofittedOpenWeatherMapProvider.class.getSimpleName();

	private final OpenWeatherMapService service;
	private final String appId;

	public RetrofittedOpenWeatherMapProvider(OpenWeatherMapService service, String appId) {
		this.service = service;
		this.appId = appId;
	}

	@Override
	public Weather getWeather(double latitude, double longitude) {
		try {
			Response<OpenWeatherMapWeather> response = service.get(latitude, longitude, "xml", appId).execute();
			Log.d(TAG, "Got response: " + response.code() + ", message: " + response.raw().toString());
			if (!response.isSuccessful()) {
				throw new UserFriendlyException(R.string.error_connection_to_weather_service_failed, response.errorBody().string());
			}
			OpenWeatherMapWeather openWeatherMapWeather = response.body();
			return new Weather(openWeatherMapWeather.getCloudPercentage());
		} catch (IOException e) {
			throw new UserFriendlyException(R.string.error_connection_to_weather_service_failed, e);
		}
	}
}
