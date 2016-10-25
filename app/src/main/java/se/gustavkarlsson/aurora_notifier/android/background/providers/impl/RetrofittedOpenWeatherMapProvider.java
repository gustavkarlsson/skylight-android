package se.gustavkarlsson.aurora_notifier.android.background.providers.impl;

import android.util.Log;

import java.io.IOException;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import se.gustavkarlsson.aurora_notifier.android.background.providers.ProviderException;
import se.gustavkarlsson.aurora_notifier.android.background.providers.Weather;
import se.gustavkarlsson.aurora_notifier.android.background.providers.WeatherProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.services.OpenWeatherMapService;
import se.gustavkarlsson.aurora_notifier.android.background.providers.services.OpenWeatherMapWeather;
import se.gustavkarlsson.aurora_notifier.common.domain.Timestamped;

// TODO Look into using their json API
// TODO Change location of APP ID
public class RetrofittedOpenWeatherMapProvider implements WeatherProvider {
	private static final String TAG = RetrofittedOpenWeatherMapProvider.class.getSimpleName();

	private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";
	private static final String APP_ID = "317cc1cbab742dfda3c96c93e7873b6e";

	private final OpenWeatherMapService service;

	RetrofittedOpenWeatherMapProvider(OpenWeatherMapService service) {
		this.service = service;
	}

	public static RetrofittedOpenWeatherMapProvider createDefault() {
		OpenWeatherMapService service = new Retrofit.Builder()
				.baseUrl(BASE_URL)
				.addConverterFactory(SimpleXmlConverterFactory.create())
				.build().create(OpenWeatherMapService.class);
		return new RetrofittedOpenWeatherMapProvider(service);
	}

	@Override
	public Timestamped<? extends Weather> getWeather(double latitude, double longitude) throws ProviderException {
		try {
			Response<OpenWeatherMapWeather> response = service.get(latitude, longitude, "xml", APP_ID).execute();
			Log.d(TAG, "Got response: " + response.code() + ", message: " + response.raw().toString());
			if (!response.isSuccessful()) {
				throw new ProviderException(response.errorBody().string());
			}
			return new Timestamped<>(response.body());
		} catch (IOException e) {
			throw new ProviderException(e);
		}
	}
}
