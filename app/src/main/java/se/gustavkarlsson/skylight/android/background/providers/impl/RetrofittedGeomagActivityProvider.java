package se.gustavkarlsson.skylight.android.background.providers.impl;

import android.util.Log;

import java.io.IOException;

import javax.inject.Inject;

import dagger.Reusable;
import retrofit2.Response;
import se.gustavkarlsson.aurora_notifier.common.domain.Timestamped;
import se.gustavkarlsson.aurora_notifier.common.service.KpIndexService;
import se.gustavkarlsson.skylight.android.R;
import se.gustavkarlsson.skylight.android.background.providers.GeomagActivityProvider;
import se.gustavkarlsson.skylight.android.models.factors.GeomagActivity;
import se.gustavkarlsson.skylight.android.util.UserFriendlyException;

@Reusable
public class RetrofittedGeomagActivityProvider implements GeomagActivityProvider {
	private static final String TAG = RetrofittedGeomagActivityProvider.class.getSimpleName();

	private final KpIndexService service;

	@Inject
	RetrofittedGeomagActivityProvider(KpIndexService service) {
		this.service = service;
	}

	@Override
	public GeomagActivity getGeomagActivity() {
		try {
			Response<Timestamped<Float>> response = service.get().execute();
			Log.d(TAG, "Got response: " + response.code() + ", message: " + response.raw().toString());
			if (!response.isSuccessful()) {
				throw new UserFriendlyException(R.string.error_could_not_determine_geomag_activity, response.errorBody().string());
			}
			Timestamped<Float> kpIndex = response.body();
			return new GeomagActivity(kpIndex.getValue());
		} catch (IOException e) {
			throw new UserFriendlyException(R.string.error_could_not_determine_geomag_activity, e);
		}
	}
}
