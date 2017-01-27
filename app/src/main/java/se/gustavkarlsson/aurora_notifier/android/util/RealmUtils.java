package se.gustavkarlsson.aurora_notifier.android.util;

import android.content.Context;
import android.util.Log;

import java.util.Arrays;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;

public class RealmUtils {
	private static final String TAG = RealmUtils.class.getSimpleName();

	private RealmUtils() {
	}

	public static void setupRealm(Context context) {
		Realm.init(context);
		deleteRealmDatabaseIfMigrationNeeded();
		try (Realm realm = Realm.getDefaultInstance()) {
			List<Class<? extends RealmObject>> classes = Arrays.asList();
			for (Class<? extends RealmObject> clazz : classes) {
				// TODO do these in a single transaction
				ensureRealmSingletonExists(realm, clazz);
			}
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
			realm.beginTransaction();
			realm.createObject(realmClass);
			realm.commitTransaction();
		} else {
			Log.d(TAG, "An instance of " + realmClass + " already exists.");
		}
	}
}
