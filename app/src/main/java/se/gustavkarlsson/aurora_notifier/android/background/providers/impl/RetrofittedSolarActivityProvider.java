package se.gustavkarlsson.aurora_notifier.android.background.providers.impl;

import android.util.Log;

import java.io.IOException;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import se.gustavkarlsson.aurora_notifier.android.background.providers.SolarActivityProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.ProviderException;
import se.gustavkarlsson.aurora_notifier.android.models.factors.SolarActivity;
import se.gustavkarlsson.aurora_notifier.common.domain.Timestamped;
import se.gustavkarlsson.aurora_notifier.common.service.KpIndexService;

public class RetrofittedSolarActivityProvider implements SolarActivityProvider {
	private static final String TAG = RetrofittedSolarActivityProvider.class.getSimpleName();

	// TODO replace with configuration
	private static final String BASE_URL = "http://9698.s.t4vps.eu/rest/";

	private final KpIndexService service;

	RetrofittedSolarActivityProvider(KpIndexService service) {
		this.service = service;
	}

	public static RetrofittedSolarActivityProvider createDefault() {
		KpIndexService service = new Retrofit.Builder()
				// TODO Update to more permanent hostname
				.baseUrl(BASE_URL)
				.addConverterFactory(GsonConverterFactory.create())
				.build().create(KpIndexService.class);
		return new RetrofittedSolarActivityProvider(service);
	}

	@Override
	public SolarActivity getSolarActivity() throws ProviderException {
		try {
			Response<Timestamped<Float>> response = service.get().execute();
			Log.d(TAG, "Got response: " + response.code() + ", message: " + response.raw().toString());
			if (!response.isSuccessful()) {
				throw new ProviderException(response.errorBody().string());
			}
			Timestamped<Float> kpIndex = response.body();
			return new SolarActivity(kpIndex.getValue());
		} catch (IOException e) {
			throw new ProviderException(e);
		}
	}
}
