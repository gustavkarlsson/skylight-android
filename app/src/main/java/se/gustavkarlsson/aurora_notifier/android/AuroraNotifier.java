package se.gustavkarlsson.aurora_notifier.android;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import se.gustavkarlsson.aurora_notifier.android.background.UpdateScheduler;
import se.gustavkarlsson.aurora_notifier.android.dagger.components.ApplicationComponent;
import se.gustavkarlsson.aurora_notifier.android.dagger.components.DaggerApplicationComponent;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.ContextModule;

public class AuroraNotifier extends Application {
	private static final String TAG = AuroraNotifier.class.getSimpleName();

	private ApplicationComponent applicationComponent;

	@Override
	public void onCreate() {
		Log.v(TAG, "onCreate");
		super.onCreate();
		this.applicationComponent = DaggerApplicationComponent.builder()
				.contextModule(new ContextModule(this))
				.build();
		UpdateScheduler.initJobManager(this);
	}

	public static ApplicationComponent getApplicationComponent(Context context) {
		return ((AuroraNotifier) context.getApplicationContext()).applicationComponent;
	}

}
