package se.gustavkarlsson.aurora_notifier.android.background;

import android.content.Intent;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;

import java.io.IOException;

import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import se.gustavkarlsson.aurora_notifier.common.domain.Timestamped;
import se.gustavkarlsson.aurora_notifier.common.service.KpIndexService;

public class AuroraPollingService extends WakefulIntentService {

	public static final String ACTION_UPDATE = "se.gustavkarlsson.aurora_notifier.android.action.UPDATE";

	private static final String TAG = AuroraPollingService.class.getSimpleName();

	private final KpIndexService kpIndexService;

	public AuroraPollingService() {
        super(AuroraPollingService.class.getSimpleName());

		// TODO Update to more permanent host
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl("http://9698.s.time4vps.eu/rest/")
				.addConverterFactory(GsonConverterFactory.create())
				.build();
		kpIndexService = retrofit.create(KpIndexService.class);
    }

	@Override
	protected void doWakefulWork(Intent intent) {
		Log.v(TAG, "doWakefulWork");
		if (intent != null) {
			final String action = intent.getAction();
			if (ACTION_UPDATE.equals(action)) {
				update();
			}
		}
	}

	private void update() {
		Log.v(TAG, "update");
		try {
			Response<Timestamped<Float>> response = kpIndexService.get().execute();
			Log.d(TAG, "Got response: " + response.code() + ", message: " + response.raw().toString());
			// TODO Do something with response
		} catch (IOException e) {
			e.printStackTrace();
		}

		throw new UnsupportedOperationException("Not yet implemented");
	}
}
