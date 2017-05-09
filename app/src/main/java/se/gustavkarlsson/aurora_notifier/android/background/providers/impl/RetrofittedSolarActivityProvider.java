package se.gustavkarlsson.aurora_notifier.android.background.providers.impl;

import android.util.Log;

import java.io.IOException;

import javax.inject.Inject;

import retrofit2.Response;
import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.background.providers.SolarActivityProvider;
import se.gustavkarlsson.aurora_notifier.android.models.data.SolarActivity;
import se.gustavkarlsson.aurora_notifier.android.util.UserFriendlyException;
import se.gustavkarlsson.aurora_notifier.common.domain.Timestamped;
import se.gustavkarlsson.aurora_notifier.common.service.KpIndexService;

public class RetrofittedSolarActivityProvider implements SolarActivityProvider {
	private static final String TAG = RetrofittedSolarActivityProvider.class.getSimpleName();

	private final KpIndexService service;

	@Inject
	RetrofittedSolarActivityProvider(KpIndexService service) {
		this.service = service;
	}

	@Override
	public SolarActivity getSolarActivity() {
		try {
			Response<Timestamped<Float>> response = service.get().execute();
			Log.d(TAG, "Got response: " + response.code() + ", message: " + response.raw().toString());
			if (!response.isSuccessful()) {
				throw new UserFriendlyException(R.string.error_connection_to_solar_activity_service_failed, response.errorBody().string());
			}
			Timestamped<Float> kpIndex = response.body();
			return new SolarActivity(kpIndex.getValue());
		} catch (IOException e) {
			throw new UserFriendlyException(R.string.error_connection_to_solar_activity_service_failed, e);
		}
	}
}
