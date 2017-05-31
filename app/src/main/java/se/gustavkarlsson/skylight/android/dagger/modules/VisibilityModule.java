package se.gustavkarlsson.skylight.android.dagger.modules;

import android.content.Context;

import dagger.Module;
import dagger.Provides;
import dagger.Reusable;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import se.gustavkarlsson.skylight.android.R;
import se.gustavkarlsson.skylight.android.background.providers.VisibilityProvider;
import se.gustavkarlsson.skylight.android.background.providers.impl.openweathermap.OpenWeatherMapService;
import se.gustavkarlsson.skylight.android.background.providers.impl.openweathermap.RetrofittedOpenWeatherMapVisibilityProvider;

@Module
public abstract class VisibilityModule {
	private static final String OPENWEATHERMAP_API_URL = "http://api.openweathermap.org/data/2.5/";

	@Provides
	@Reusable
	static OpenWeatherMapService provideOpenWeatherMapService() {
		return new Retrofit.Builder()
				.baseUrl(OPENWEATHERMAP_API_URL)
				.addConverterFactory(GsonConverterFactory.create())
				.build().create(OpenWeatherMapService.class);
	}

	@Provides
	@Reusable
	static VisibilityProvider provideVisibilityProvider(Context context, OpenWeatherMapService openWeatherMapService) {
		String apiKey = context.getString(R.string.api_key_openweathermap);
		return new RetrofittedOpenWeatherMapVisibilityProvider(openWeatherMapService, apiKey);
	}
}
