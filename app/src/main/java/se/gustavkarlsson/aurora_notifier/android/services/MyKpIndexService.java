package se.gustavkarlsson.aurora_notifier.android.services;

import android.util.Log;

import java.io.IOException;

import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import se.gustavkarlsson.aurora_notifier.common.domain.Timestamped;

public class MyKpIndexService implements KpIndexService {

	private static final String TAG = MyKpIndexService.class.getSimpleName();

	private final se.gustavkarlsson.aurora_notifier.common.service.KpIndexService service;

	public MyKpIndexService() {
		Retrofit retrofit = new Retrofit.Builder()
				// TODO Update to more permanent host
				.baseUrl("http://9698.s.time4vps.eu/rest/")
				.addConverterFactory(GsonConverterFactory.create())
				.build();
		service = retrofit.create(se.gustavkarlsson.aurora_notifier.common.service.KpIndexService.class);
	}

	@Override
	public Timestamped<Float> getKpIndex() throws ServiceException {
		try {
			Response<Timestamped<Float>> response = service.get().execute();
			Log.d(TAG, "Got response: " + response.code() + ", message: " + response.raw().toString());
			if (!response.isSuccess()) {
				throw new ServiceException(response.errorBody().string());
			}
			return response.body();
		} catch (IOException e) {
			throw new ServiceException(e);
		}
	}
}
