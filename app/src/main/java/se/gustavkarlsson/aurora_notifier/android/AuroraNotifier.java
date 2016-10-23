package se.gustavkarlsson.aurora_notifier.android;

import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.commonsware.cwac.wakeful.WakefulIntentService;

import io.realm.Realm;
import se.gustavkarlsson.aurora_notifier.android.background.AuroraPollingService;
import se.gustavkarlsson.aurora_notifier.android.background.BootReceiver;
import se.gustavkarlsson.aurora_notifier.android.realm.RealmKpIndex;
import se.gustavkarlsson.aurora_notifier.android.realm.RealmWeather;

public class AuroraNotifier extends Application {
	private static final String TAG = AuroraNotifier.class.getSimpleName();

	@Override
	public void onCreate() {
		Log.v(TAG, "onCreate");
		super.onCreate();
		setupRealm();
		setupAlarm();
		Intent updateIntent = AuroraPollingService.createUpdateIntent(this);
		WakefulIntentService.sendWakefulWork(this, updateIntent);
	}

	private void setupRealm() {
		Realm.init(this);
		Realm realm = Realm.getDefaultInstance();
		ensureRealmKpIndexExists(realm);
		ensureRealmWeatherExists(realm);
		realm.close();
	}

	private static void ensureRealmKpIndexExists(Realm realm) {
		if (realm.where(RealmKpIndex.class).count() == 0) {
			Log.i(TAG, "No RealmKpIndex exists. Creating one");
			realm.beginTransaction();
			realm.createObject(RealmKpIndex.class);
			realm.commitTransaction();
		} else {
			Log.i(TAG, "A RealmKpIndex already exists. Will not create one");
		}
	}

	private static void ensureRealmWeatherExists(Realm realm) {
		if (realm.where(RealmWeather.class).count() == 0) {
			Log.i(TAG, "No RealmWeather exists. Creating one");
			realm.beginTransaction();
			realm.createObject(RealmWeather.class);
			realm.commitTransaction();
		} else {
			Log.i(TAG, "A RealmWeather already exists. Will not create one");
		}
	}

	private void setupAlarm() {
		Log.v(TAG, "setupAlarm");
		LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);
		Intent intent = new Intent(BootReceiver.ACTION_SETUP_ALARMS, null, this, BootReceiver.class);
		localBroadcastManager.registerReceiver(new BootReceiver(), new IntentFilter(BootReceiver.ACTION_SETUP_ALARMS));
		localBroadcastManager.sendBroadcast(intent);
	}
}
