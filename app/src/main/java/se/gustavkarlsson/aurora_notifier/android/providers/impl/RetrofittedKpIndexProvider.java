package se.gustavkarlsson.aurora_notifier.android.providers.impl;

import java.io.IOException;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import se.gustavkarlsson.aurora_notifier.android.providers.KpIndexProvider;
import se.gustavkarlsson.aurora_notifier.android.providers.ProviderException;
import se.gustavkarlsson.aurora_notifier.common.domain.Timestamped;
import se.gustavkarlsson.aurora_notifier.common.service.KpIndexService;

public class RetrofittedKpIndexProvider implements KpIndexProvider {
	private static final String TAG = RetrofittedKpIndexProvider.class.getSimpleName();

	private static final String BASE_URL = "http://9698.s.time4vps.eu/rest/";

	private final KpIndexService service;

	RetrofittedKpIndexProvider(KpIndexService service) {
		this.service = service;
	}

	public static RetrofittedKpIndexProvider createDefault() {
		KpIndexService service = new Retrofit.Builder()
				// TODO Update to more permanent host
				.baseUrl(BASE_URL)
				.addConverterFactory(GsonConverterFactory.create())
				.build().create(KpIndexService.class);
		return new RetrofittedKpIndexProvider(service);
	}

	@Override
	public Timestamped<Float> getKpIndex() throws ProviderException {
		try {
			Response<Timestamped<Float>> response = service.get().execute();
			//Log.d(TAG, "Got response: " + response.code() + ", message: " + response.raw().toString()); TODO Needs mock for testing
			if (!response.isSuccessful()) {
				throw new ProviderException(response.errorBody().string());
			}
			return response.body();
		} catch (IOException e) {
			throw new ProviderException(e);
		}
	}
}
