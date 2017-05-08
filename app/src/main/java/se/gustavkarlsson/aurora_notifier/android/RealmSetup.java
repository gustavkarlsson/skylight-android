package se.gustavkarlsson.aurora_notifier.android;

import android.content.Context;

import io.realm.Realm;

class RealmSetup {

	private RealmSetup() {
	}

	static void run(Context context) {
		Realm.init(context);
	}
}
