package se.gustavkarlsson.aurora_notifier.android.background;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;

import org.parceler.Parcels;

import se.gustavkarlsson.aurora_notifier.android.notifications.AuroraNotificationSender;
import se.gustavkarlsson.aurora_notifier.android.notifications.NotificationSender;
import se.gustavkarlsson.aurora_notifier.android.parcels.AuroraUpdate;
import se.gustavkarlsson.aurora_notifier.android.services.KpIndexService;
import se.gustavkarlsson.aurora_notifier.android.services.RetrofittedKpIndexService;
import se.gustavkarlsson.aurora_notifier.android.services.RetrofittedOpenWeatherMapService;
import se.gustavkarlsson.aurora_notifier.android.services.ServiceException;
import se.gustavkarlsson.aurora_notifier.android.services.Weather;
import se.gustavkarlsson.aurora_notifier.android.services.WeatherService;
import se.gustavkarlsson.aurora_notifier.common.domain.Timestamped;

public class AuroraPollingService extends WakefulIntentService {

	private static final String TAG = AuroraPollingService.class.getSimpleName();
	public static final String ACTION_UPDATED = TAG + ".AURORA_UPDATED";
	private static final String ACTION_UPDATE = TAG + ".UPDATE";

	private KpIndexService kpIndexService;
	private WeatherService weatherService;
	private NotificationSender<Timestamped<Float>> notificationSender;
	private LocalBroadcastManager broadcaster;

	// Default constructor required
	public AuroraPollingService() {
		super(AuroraPollingService.class.getSimpleName());
	}

	@Override
	public void onCreate() {
		super.onCreate();
		kpIndexService = new RetrofittedKpIndexService();
		weatherService = RetrofittedOpenWeatherMapService.createDefault();
		notificationSender = new AuroraNotificationSender(this, (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE));
		broadcaster = LocalBroadcastManager.getInstance(this);
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
			Timestamped<Float> kpIndex = kpIndexService.getKpIndex();
			Timestamped<Weather> weather = weatherService.getWeather(63.8342338, 20.2744067); // TODO reset and change appid
			broadcaster.sendBroadcast(createUpdatedIntent(kpIndex));
			notificationSender.notify(kpIndex);
		} catch (ServiceException e) {
			// TODO Handle error better
			e.printStackTrace();
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
