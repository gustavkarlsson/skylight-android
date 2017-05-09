package se.gustavkarlsson.aurora_notifier.android.dagger.modules;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import dagger.Reusable;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.background.providers.WeatherProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.impl.OpenWeatherMapService;
import se.gustavkarlsson.aurora_notifier.android.background.providers.impl.RetrofittedOpenWeatherMapProvider;

@Module
public abstract class WeatherModule {
	private static final String API_URL = "http://api.openweathermap.org/data/2.5/";

	@Provides
	@Reusable
	static OpenWeatherMapService provideOpenWeatherMapService() {
		return new Retrofit.Builder()
				.baseUrl(API_URL)
				.addConverterFactory(GsonConverterFactory.create())
				.build().create(OpenWeatherMapService.class);
	}

	@Provides
	@Reusable
	static WeatherProvider provideWeatherProvider(Context context, OpenWeatherMapService openWeatherMapService) {
		String apiKey = context.getString(R.string.api_key_openweathermap);
		return new RetrofittedOpenWeatherMapProvider(openWeatherMapService, apiKey);
	}
}
