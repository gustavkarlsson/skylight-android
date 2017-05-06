package se.gustavkarlsson.aurora_notifier.android.dagger.modules;

import dagger.Module;
import dagger.Provides;
import dagger.Reusable;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import se.gustavkarlsson.aurora_notifier.android.background.providers.WeatherProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.impl.OpenWeatherMapService;
import se.gustavkarlsson.aurora_notifier.android.background.providers.impl.RetrofittedOpenWeatherMapProvider;

@Module
public class WeatherModule {
	private static final String API_URL = "http://api.openweathermap.org/data/2.5/";

	private final String openWeatherMapApiKey;

	public WeatherModule(String openWeatherMapApiKey) {
		this.openWeatherMapApiKey = openWeatherMapApiKey;
	}

	@Provides
	@Reusable
	static OpenWeatherMapService provideOpenWeatherMapService() {
		return new Retrofit.Builder()
				.baseUrl(API_URL)
				.addConverterFactory(SimpleXmlConverterFactory.create())
				.build().create(OpenWeatherMapService.class);
	}

	@Provides
	@Reusable
	WeatherProvider provideWeatherProvider(OpenWeatherMapService openWeatherMapService) {
		return new RetrofittedOpenWeatherMapProvider(openWeatherMapService, openWeatherMapApiKey);
	}
}
