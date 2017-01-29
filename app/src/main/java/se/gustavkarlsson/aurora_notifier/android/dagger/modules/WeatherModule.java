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

	@Provides
	@Reusable
	static OpenWeatherMapService provideOpenWeatherMapService() {
		return new Retrofit.Builder()
				.baseUrl("http://api.openweathermap.org/data/2.5/")
				.addConverterFactory(SimpleXmlConverterFactory.create())
				.build().create(OpenWeatherMapService.class);
	}

	@Provides
	@Reusable
	static WeatherProvider provideWeatherProvider(OpenWeatherMapService openWeatherMapService) {
		// TODO Look into getting a new API key
		return new RetrofittedOpenWeatherMapProvider(openWeatherMapService, "317cc1cbab742dfda3c96c93e7873b6e");
	}
}
