package se.gustavkarlsson.aurora_notifier.android.background;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;

import io.realm.Realm;
import se.gustavkarlsson.aurora_notifier.android.background.providers.KpIndexProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.ProviderException;
import se.gustavkarlsson.aurora_notifier.android.background.providers.Weather;
import se.gustavkarlsson.aurora_notifier.android.background.providers.WeatherProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.impl.RetrofittedKpIndexProvider;
import se.gustavkarlsson.aurora_notifier.android.background.providers.impl.RetrofittedOpenWeatherMapProvider;
import se.gustavkarlsson.aurora_notifier.android.realm.RealmKpIndex;
import se.gustavkarlsson.aurora_notifier.android.realm.RealmWeather;
import se.gustavkarlsson.aurora_notifier.common.domain.Timestamped;

public class AuroraPollingService extends WakefulIntentService {

	private static final String TAG = AuroraPollingService.class.getSimpleName();
	private static final String ACTION_UPDATE = TAG + ".UPDATE";

	private KpIndexProvider kpIndexProvider;
	private WeatherProvider weatherProvider;

	// Default constructor required
	public AuroraPollingService() {
		super(AuroraPollingService.class.getSimpleName());
	}

	@Override
	public void onCreate() {
		super.onCreate();
		kpIndexProvider = RetrofittedKpIndexProvider.createDefault();
		weatherProvider = RetrofittedOpenWeatherMapProvider.createDefault();
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
			Realm realm = Realm.getDefaultInstance();
			Log.d(TAG, "Getting KP index...");
			Timestamped<Float> kpIndex = kpIndexProvider.getKpIndex();
			Log.d(TAG, "KP Index is: " + kpIndex);

			Log.d(TAG, "Getting Weather...");
			Timestamped<? extends Weather> weather = weatherProvider.getWeather(63.8342338, 20.2744067); // TODO set coordinates properly and change appid
			Log.d(TAG, "Weather is:  " + weather);

			Log.d(TAG, "Looking up KP index from realm...");
			RealmKpIndex realmKpIndex = realm.where(RealmKpIndex.class).findFirst();
			Log.d(TAG, "Realm KP index is:  " + realmKpIndex);

			Log.d(TAG, "Looking up Weather from realm...");
			RealmWeather realmWeather = realm.where(RealmWeather.class).findFirst();
			Log.d(TAG, "Realm Weather is:  " + realmWeather);

			Log.d(TAG, "Storing to realm");
			realm.beginTransaction();
			realmKpIndex.setKpIndex(kpIndex.getValue());
			realmKpIndex.setTimestamp(kpIndex.getTimestamp());
			realmWeather.setCloudPercentage(weather.getValue().getCloudPercentage());
			realmWeather.setTimestamp(weather.getTimestamp());
			realm.commitTransaction();
			realm.close();
			Log.d(TAG, "Stored in realm");
		} catch (ProviderException e) {
			// TODO Handle error better
			e.printStackTrace();
		}
	}

	public static Intent createUpdateIntent(Context context) {
		return new Intent(ACTION_UPDATE, null, context, AuroraPollingService.class);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
