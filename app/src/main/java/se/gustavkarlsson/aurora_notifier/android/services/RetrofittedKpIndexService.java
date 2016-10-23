package se.gustavkarlsson.aurora_notifier.android.services;

import android.util.Log;

import java.io.IOException;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import se.gustavkarlsson.aurora_notifier.common.domain.Timestamped;


public class RetrofittedKpIndexService implements KpIndexService {

	private static final String TAG = RetrofittedKpIndexService.class.getSimpleName();
	private static final String BASE_URL = "http://9698.s.time4vps.eu/rest/";
	private final se.gustavkarlsson.aurora_notifier.common.service.KpIndexService service;

	public RetrofittedKpIndexService() {
		Retrofit retrofit = new Retrofit.Builder()
				// TODO Update to more permanent host
				.baseUrl(BASE_URL)
				.addConverterFactory(GsonConverterFactory.create())
				.build();
		service = retrofit.create(se.gustavkarlsson.aurora_notifier.common.service.KpIndexService.class);
	}

	@Override
	public Timestamped<Float> getKpIndex() throws ServiceException {
		try {
			Response<Timestamped<Float>> response = service.get().execute();
			Log.d(TAG, "Got response: " + response.code() + ", message: " + response.raw().toString());
			if (!response.isSuccessful()) {
				throw new ServiceException(response.errorBody().string());
			}
			return response.body();
		} catch (IOException e) {
			throw new ServiceException(e);
		}
	}
}
