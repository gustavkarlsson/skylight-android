package se.gustavkarlsson.skylight.android;

import android.app.Application;
import android.util.Log;

import com.evernote.android.job.JobManager;
import com.jakewharton.threetenabp.AndroidThreeTen;

import se.gustavkarlsson.skylight.android.dagger.components.ApplicationComponent;
import se.gustavkarlsson.skylight.android.dagger.components.DaggerApplicationComponent;
import se.gustavkarlsson.skylight.android.dagger.components.DaggerUpdateJobComponent;
import se.gustavkarlsson.skylight.android.dagger.modules.definitive.ContextModule;

import static se.gustavkarlsson.skylight.android.background.UpdateJob.UPDATE_JOB_TAG;

public class Skylight extends Application {
	private static final String TAG = Skylight.class.getSimpleName();

	private static ApplicationComponent applicationComponent;

	@Override
	public void onCreate() {
		Log.v(TAG, "onCreate");
		super.onCreate();
		AndroidThreeTen.init(this);
		applicationComponent = DaggerApplicationComponent.builder()
				.contextModule(new ContextModule(this))
				.build();
		initJobManager();
	}

	public static ApplicationComponent getApplicationComponent() {
		return applicationComponent;
	}

	private void initJobManager() {
		JobManager.create(this).addJobCreator(tag -> {
			if (tag.equals(UPDATE_JOB_TAG)) {
				return DaggerUpdateJobComponent.builder()
						.applicationComponent(applicationComponent)
						.build()
						.getUpdateJob();
			}
			return null;
		});
	}

}
