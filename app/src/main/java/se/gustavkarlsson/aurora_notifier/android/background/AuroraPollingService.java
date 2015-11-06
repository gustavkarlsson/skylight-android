package se.gustavkarlsson.aurora_notifier.android.background;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;

import java.io.IOException;

import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;
import se.gustavkarlsson.aurora_notifier.android.notifications.AuroraNotificationSender;
import se.gustavkarlsson.aurora_notifier.android.notifications.NotificationSender;
import se.gustavkarlsson.aurora_notifier.common.domain.Timestamped;
import se.gustavkarlsson.aurora_notifier.common.service.KpIndexService;

public class AuroraPollingService extends WakefulIntentService {

	public static final String ACTION_UPDATE = "se.gustavkarlsson.aurora_notifier.android.action.UPDATE";

	private static final String TAG = AuroraPollingService.class.getSimpleName();

	private final KpIndexService kpIndexService;
	private final NotificationSender<Timestamped<Float>> notificationSender;

	AuroraPollingService(KpIndexService kpIndexService, NotificationSender<Timestamped<Float>> notificationSender) {
		super(AuroraPollingService.class.getSimpleName());
		this.kpIndexService = kpIndexService;
		this.notificationSender = notificationSender;
	}

	// Default constructor required
	public AuroraPollingService() {
		super(AuroraPollingService.class.getSimpleName());
		this.kpIndexService = createKpIndexService();
		this.notificationSender = createNotificationSender();
	}

	private static KpIndexService createKpIndexService() {
		// TODO Update to more permanent host
		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl("http://9698.s.time4vps.eu/rest/")
				.addConverterFactory(GsonConverterFactory.create())
				.build();
		return retrofit.create(KpIndexService.class);
	}

	private AuroraNotificationSender createNotificationSender() {
		return new AuroraNotificationSender(this, (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE));
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
				notificationSender.notify(response.body());
				// TODO handle more data
			}
		} catch (IOException e) {
			e.printStackTrace();
			// TODO Handle error
		}
	}
}
