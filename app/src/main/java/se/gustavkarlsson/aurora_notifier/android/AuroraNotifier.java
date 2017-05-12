package se.gustavkarlsson.aurora_notifier.android;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.evernote.android.job.JobManager;

import java.io.PrintWriter;
import java.io.StringWriter;

import se.gustavkarlsson.aurora_notifier.android.background.UpdateJob;
import se.gustavkarlsson.aurora_notifier.android.dagger.components.ApplicationComponent;
import se.gustavkarlsson.aurora_notifier.android.dagger.components.DaggerApplicationComponent;
import se.gustavkarlsson.aurora_notifier.android.dagger.modules.ApplicationModule;

import static se.gustavkarlsson.aurora_notifier.android.background.UpdateJob.UPDATE_JOB_TAG;

public class AuroraNotifier extends Application {
	private static final String TAG = AuroraNotifier.class.getSimpleName();

	private ApplicationComponent applicationComponent;

	@Override
	public void onCreate() {
		Log.v(TAG, "onCreate");
		setExceptionHandler();
		super.onCreate();
		this.applicationComponent = DaggerApplicationComponent.builder()
				.applicationModule(new ApplicationModule(this))
				.build();
		initJobManager();
	}

	public static ApplicationComponent getApplicationComponent(Context context) {
		return ((AuroraNotifier) context.getApplicationContext()).applicationComponent;
	}

	private static void setExceptionHandler() {
		if (BuildConfig.DEBUG && !(Thread.getDefaultUncaughtExceptionHandler() instanceof LoggingUncaughtExceptionHandler)) {
			Thread.setDefaultUncaughtExceptionHandler(new LoggingUncaughtExceptionHandler());
		}
	}

	private void initJobManager() {
		JobManager.create(this).addJobCreator(tag -> {
			if (tag.equals(UPDATE_JOB_TAG)) {
				return new UpdateJob();
			}
			return null;
		});
	}

	private static class LoggingUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
		@Override
		public void uncaughtException(Thread thread, Throwable throwable) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			throwable.printStackTrace(pw);
			String stackTrace = sw.toString();
			Log.wtf(TAG, "The application has crashed!\n" + stackTrace);
			System.exit(-1);
		}
	}

}
