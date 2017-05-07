package se.gustavkarlsson.aurora_notifier.android;

import android.content.Context;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import se.gustavkarlsson.aurora_notifier.android.realm.EvaluationCache;
import se.gustavkarlsson.aurora_notifier.android.realm.Requirements;
import se.gustavkarlsson.aurora_notifier.android.realm.Settings;

class RealmSetup {
	private static final String TAG = RealmSetup.class.getSimpleName();

	private RealmSetup() {
	}

	static void run(Context context) {
		Realm.init(context);
		deleteRealmDatabaseIfMigrationNeeded();
		try (Realm realm = Realm.getDefaultInstance()) {
			List<Class<? extends RealmObject>> classes = Arrays.asList(Requirements.class, EvaluationCache.class);
			for (Class<? extends RealmObject> clazz : classes) {
				ensureRealmSingletonExists(realm, clazz);
			}
			ensureSettingsExist(realm, context);
		}
	}

	private static void deleteRealmDatabaseIfMigrationNeeded() {
		RealmConfiguration config = new RealmConfiguration.Builder()
				.deleteRealmIfMigrationNeeded()
				.build();
		Realm.setDefaultConfiguration(config);
	}

	private static void ensureRealmSingletonExists(Realm realm, Class<? extends RealmObject> realmClass) {
		Log.i(TAG, "Ensuring that an instance of " + realmClass.getSimpleName() + " exists.");
		if (realm.where(realmClass).count() == 0) {
			Log.d(TAG, "No instance of " + realmClass.getSimpleName() + " exists. Creating one");
			realm.executeTransaction(r -> realm.createObject(realmClass));
		} else {
			Log.d(TAG, "An instance of " + realmClass + " already exists.");
		}
	}

	private static void ensureSettingsExist(Realm realm, Context context) {
		Log.i(TAG, "Ensuring that Settings exist");
		if (realm.where(Settings.class).count() == 0) {
			Log.d(TAG, "No instance of " + Settings.class.getSimpleName() + " exists. Creating one with defaults");
			realm.executeTransaction(r -> {
				Settings settings = realm.createObject(Settings.class);
				settings.setNotifications(context.getResources().getBoolean(R.bool.default_notifications_enabled));
			});
		} else {
			Log.d(TAG, "An instance of " + Settings.class + " already exists.");
		}
	}
}
