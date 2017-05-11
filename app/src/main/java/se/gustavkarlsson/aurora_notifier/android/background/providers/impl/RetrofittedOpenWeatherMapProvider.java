package se.gustavkarlsson.aurora_notifier.android.background.providers.impl;

import android.util.Log;

import java.io.IOException;

import retrofit2.Response;
import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.background.providers.WeatherProvider;
import se.gustavkarlsson.aurora_notifier.android.models.data.Weather;
import se.gustavkarlsson.aurora_notifier.android.util.UserFriendlyException;

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
			Response<OpenWeatherMapWeather> response = service.get(latitude, longitude, "json", appId).execute();
			Log.d(TAG, "Got response: " + response.code() + ", message: " + response.raw().toString());
			if (!response.isSuccessful()) {
				throw new UserFriendlyException(R.string.error_could_not_determine_weather, response.errorBody().string());
			}
			OpenWeatherMapWeather openWeatherMapWeather = response.body();
			return new Weather(openWeatherMapWeather.getClouds().getAll());
		} catch (IOException e) {
			throw new UserFriendlyException(R.string.error_could_not_determine_weather, e);
		}
	}
}
