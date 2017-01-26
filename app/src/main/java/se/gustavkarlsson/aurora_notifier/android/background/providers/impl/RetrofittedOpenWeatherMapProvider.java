package se.gustavkarlsson.aurora_notifier.android.background.providers.impl;

import android.util.Log;

import java.io.IOException;

import retrofit2.Response;
import se.gustavkarlsson.aurora_notifier.android.background.providers.ProviderException;
import se.gustavkarlsson.aurora_notifier.android.background.providers.WeatherProvider;
import se.gustavkarlsson.aurora_notifier.android.models.factors.Weather;

// TODO Look into using their json API
// TODO Change location of APP ID
public class RetrofittedOpenWeatherMapProvider implements WeatherProvider {
	private static final String TAG = RetrofittedOpenWeatherMapProvider.class.getSimpleName();

	private final OpenWeatherMapService service;
	private final String appId;

	public RetrofittedOpenWeatherMapProvider(OpenWeatherMapService service, String appId) {
		this.service = service;
		this.appId = appId;
	}

	@Override
	public Weather getWeather(double latitude, double longitude) throws ProviderException {
		try {
			Response<OpenWeatherMapWeather> response = service.get(latitude, longitude, "xml", appId).execute();
			Log.d(TAG, "Got response: " + response.code() + ", message: " + response.raw().toString());
			if (!response.isSuccessful()) {
				throw new ProviderException(response.errorBody().string());
			}
			OpenWeatherMapWeather openWeatherMapWeather = response.body();
			return new Weather(openWeatherMapWeather.getCloudPercentage());
		} catch (IOException e) {
			throw new ProviderException(e);
		}
	}
}
