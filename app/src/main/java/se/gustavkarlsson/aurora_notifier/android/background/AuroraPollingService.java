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
import se.gustavkarlsson.aurora_notifier.android.background.providers.KpIndexProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.ProviderException;
import se.gustavkarlsson.aurora_notifier.android.background.providers.Weather;
import se.gustavkarlsson.aurora_notifier.android.background.providers.WeatherProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.impl.RetrofittedKpIndexProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.impl.RetrofittedOpenWeatherMapProvider;
import se.gustavkarlsson.aurora_notifier.common.domain.Timestamped;

public class AuroraPollingService extends WakefulIntentService {

	private static final String TAG = AuroraPollingService.class.getSimpleName();
	public static final String ACTION_UPDATED = TAG + ".AURORA_UPDATED";
	private static final String ACTION_UPDATE = TAG + ".UPDATE";

	private KpIndexProvider kpIndexProvider;
	private WeatherProvider weatherProvider;
	private NotificationSender<Timestamped<Float>> notificationSender;
	private LocalBroadcastManager broadcaster;

	// Default constructor required
	public AuroraPollingService() {
		super(AuroraPollingService.class.getSimpleName());
	}

	@Override
	public void onCreate() {
		super.onCreate();
		kpIndexProvider = RetrofittedKpIndexProvider.createDefault();
		weatherProvider = RetrofittedOpenWeatherMapProvider.createDefault();
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
			Timestamped<Float> kpIndex = kpIndexProvider.getKpIndex();
			broadcaster.sendBroadcast(createUpdatedIntent(kpIndex));
			notificationSender.notify(kpIndex);
			Timestamped<? extends Weather> weather = weatherProvider.getWeather(63.8342338, 20.2744067); // TODO reset and change appid
			// TODO broadcast weather updates
		} catch (ProviderException e) {
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
