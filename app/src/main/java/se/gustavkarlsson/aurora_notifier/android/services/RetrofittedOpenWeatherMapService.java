package se.gustavkarlsson.aurora_notifier.android.services;

import java.io.IOException;


import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import se.gustavkarlsson.aurora_notifier.common.domain.Timestamped;

public class RetrofittedOpenWeatherMapService implements WeatherService {

	private static final String TAG = RetrofittedOpenWeatherMapService.class.getSimpleName();

	private final OpenWeatherMapService service;

	public RetrofittedOpenWeatherMapService(OpenWeatherMapService service) {
		this.service = service;
	}

	public static RetrofittedOpenWeatherMapService createDefault() {
		OpenWeatherMapService service = new Retrofit.Builder()
				.baseUrl(OpenWeatherMapService.BASE_URL)
				.addConverterFactory(SimpleXmlConverterFactory.create())
				.build().create(OpenWeatherMapService.class);
		return new RetrofittedOpenWeatherMapService(service);
	}

	@Override
	public Timestamped<Weather> getWeather(double latitude, double longitude) throws ServiceException {
		try {
			Response<Weather> response = service.get(latitude, longitude, "xml", OpenWeatherMapService.APP_ID).execute();
			//Log.d(TAG, "Got response: " + response.code() + ", message: " + response.raw().toString());
			if (!response.isSuccessful()) {
				throw new ServiceException(response.errorBody().string());
			}
			return new Timestamped<>(response.body());
		} catch (IOException e) {
			throw new ServiceException(e);
		}
	}
}
