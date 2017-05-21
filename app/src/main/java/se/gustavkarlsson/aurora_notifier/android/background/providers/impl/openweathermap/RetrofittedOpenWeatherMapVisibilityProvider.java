package se.gustavkarlsson.aurora_notifier.android.background.providers.impl.openweathermap;

import android.util.Log;

import java.io.IOException;

import retrofit2.Response;
import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.background.providers.VisibilityProvider;
import se.gustavkarlsson.aurora_notifier.android.models.factors.Visibility;
import se.gustavkarlsson.aurora_notifier.android.util.UserFriendlyException;

public class RetrofittedOpenWeatherMapVisibilityProvider implements VisibilityProvider {
	private static final String TAG = RetrofittedOpenWeatherMapVisibilityProvider.class.getSimpleName();

	private final OpenWeatherMapService service;
	private final String appId;

	public RetrofittedOpenWeatherMapVisibilityProvider(OpenWeatherMapService service, String appId) {
		this.service = service;
		this.appId = appId;
	}

	@Override
	public Visibility getVisibility(double latitude, double longitude) {
		try {
			Response<OpenWeatherMapWeather> response = service.get(latitude, longitude, "json", appId).execute();
			Log.d(TAG, "Got response: " + response.code() + ", message: " + response.raw().toString());
			if (!response.isSuccessful()) {
				throw new UserFriendlyException(R.string.error_could_not_determine_visibility, response.errorBody().string());
			}
			OpenWeatherMapWeather openWeatherMapWeather = response.body();
			return new Visibility(openWeatherMapWeather.getClouds().getAll());
		} catch (IOException e) {
			throw new UserFriendlyException(R.string.error_could_not_determine_visibility, e);
		}
	}
}
