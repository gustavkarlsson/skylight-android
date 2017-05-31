package se.gustavkarlsson.aurora_notifier.android;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.evernote.android.job.JobManager;
import com.jakewharton.threetenabp.AndroidThreeTen;

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
		super.onCreate();
		AndroidThreeTen.init(this);
		this.applicationComponent = DaggerApplicationComponent.builder()
				.applicationModule(new ApplicationModule(this))
				.build();
		initJobManager();
	}

	public static ApplicationComponent getApplicationComponent(Context context) {
		return ((AuroraNotifier) context.getApplicationContext()).applicationComponent;
	}

	private void initJobManager() {
		JobManager.create(this).addJobCreator(tag -> {
			if (tag.equals(UPDATE_JOB_TAG)) {
				return new UpdateJob();
			}
			return null;
		});
	}

}
