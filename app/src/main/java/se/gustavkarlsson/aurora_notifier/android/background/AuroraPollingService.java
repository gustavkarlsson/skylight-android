package se.gustavkarlsson.aurora_notifier.android.background;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;

import org.parceler.Parcels;

import java.io.IOException;

import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import se.gustavkarlsson.aurora_notifier.android.notifications.AuroraNotificationSender;
import se.gustavkarlsson.aurora_notifier.android.notifications.NotificationSender;
import se.gustavkarlsson.aurora_notifier.android.parcels.AuroraUpdate;
import se.gustavkarlsson.aurora_notifier.common.domain.Timestamped;
import se.gustavkarlsson.aurora_notifier.common.service.KpIndexService;

public class AuroraPollingService extends WakefulIntentService {

	private static final String TAG = AuroraPollingService.class.getSimpleName();
	public static final String ACTION_UPDATED = TAG + ".AURORA_UPDATED";
	private static final String ACTION_UPDATE = TAG + ".UPDATE";

	private KpIndexService kpIndexService;
	private NotificationSender<Timestamped<Float>> notificationSender;
	private LocalBroadcastManager broadcaster;

	// Default constructor required
	public AuroraPollingService() {
		super(AuroraPollingService.class.getSimpleName());
	}

	@Override
	public void onCreate() {
		super.onCreate();
		kpIndexService = createKpIndexService();
		notificationSender = new AuroraNotificationSender(this, (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE));
		broadcaster = LocalBroadcastManager.getInstance(this);
	}

	private KpIndexService createKpIndexService() {
		Retrofit retrofit = new Retrofit.Builder()
				// TODO Update to more permanent host
				.baseUrl("http://9698.s.time4vps.eu/rest/")
				.addConverterFactory(GsonConverterFactory.create())
				.build();
		return retrofit.create(KpIndexService.class);
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

	public void update() {
		Log.v(TAG, "update");
		try {
			Response<Timestamped<Float>> response = kpIndexService.get().execute();
			Log.d(TAG, "Got response: " + response.code() + ", message: " + response.raw().toString());
			if (response.isSuccess()) {
				Timestamped<Float> kpIndex = response.body();
				broadcaster.sendBroadcast(createUpdatedIntent(kpIndex));
				notificationSender.notify(kpIndex);
			}
		} catch (IOException e) {
			e.printStackTrace();
			// TODO Handle error
		}
	}

	public static Intent createUpdateIntent(Context context) {
		return new Intent(ACTION_UPDATE, null, context, AuroraPollingService.class);
	}

	private Intent createUpdatedIntent(Timestamped<Float> kpIndex) {
		Intent intent = new Intent(ACTION_UPDATED);
		intent.putExtra(AuroraUpdate.TAG, Parcels.wrap(new AuroraUpdate(kpIndex)));
		return intent;
	}
}
