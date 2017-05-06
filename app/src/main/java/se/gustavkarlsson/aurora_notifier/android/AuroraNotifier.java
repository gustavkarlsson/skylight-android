package se.gustavkarlsson.aurora_notifier.android;

import android.app.Application;
import android.util.Log;

import se.gustavkarlsson.aurora_notifier.android.background.UpdateScheduler;

public class AuroraNotifier extends Application {
	private static final String TAG = AuroraNotifier.class.getSimpleName();

	@Override
	public void onCreate() {
		Log.v(TAG, "onCreate");
		super.onCreate();
		RealmSetup.run(this);
		UpdateScheduler.initJobManager(this);
	}

}
