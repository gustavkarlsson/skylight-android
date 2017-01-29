package se.gustavkarlsson.aurora_notifier.android;

import android.app.Application;
import android.util.Log;

import se.gustavkarlsson.aurora_notifier.android.background.ScheduleUpdatesBootReceiver;
import se.gustavkarlsson.aurora_notifier.android.util.RealmUtils;

public class AuroraNotifier extends Application {
	private static final String TAG = AuroraNotifier.class.getSimpleName();

	@Override
	public void onCreate() {
		Log.v(TAG, "onCreate");
		super.onCreate();
		RealmUtils.setupRealm(this);
	}

}
