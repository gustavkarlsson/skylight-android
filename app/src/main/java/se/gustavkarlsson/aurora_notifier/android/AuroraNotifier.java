package se.gustavkarlsson.aurora_notifier.android;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

import se.gustavkarlsson.aurora_notifier.android.background.UpdateScheduler;
import se.gustavkarlsson.aurora_notifier.android.dagger.components.ApplicationComponent;
import se.gustavkarlsson.aurora_notifier.android.dagger.components.DaggerApplicationComponent;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.ApplicationModule;

public class AuroraNotifier extends Application {
	private static final String TAG = AuroraNotifier.class.getSimpleName();

	private ApplicationComponent applicationComponent;

	@Override
	public void onCreate() {
		Thread.setDefaultUncaughtExceptionHandler(new LoggingUncaughtExceptionHandler());
		Log.v(TAG, "onCreate");
		super.onCreate();
		this.applicationComponent = DaggerApplicationComponent.builder()
				.applicationModule(new ApplicationModule(this))
				.build();
		UpdateScheduler.initJobManager(this);
	}

	public static ApplicationComponent getApplicationComponent(Context context) {
		return ((AuroraNotifier) context.getApplicationContext()).applicationComponent;
	}

	private static class LoggingUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
		@Override
		public void uncaughtException(Thread thread, Throwable throwable) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			throwable.printStackTrace(pw);
			String stackTrace = sw.toString();
			Log.e(TAG, "The application has crashed!\n" + stackTrace);
			System.exit(-1);
		}
	}

}
