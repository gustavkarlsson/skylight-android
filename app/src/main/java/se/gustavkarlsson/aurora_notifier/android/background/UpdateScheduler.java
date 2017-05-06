package se.gustavkarlsson.aurora_notifier.android.background;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.realm.Requirements;

public class UpdateScheduler {
	private static final String TAG = UpdateScheduler.class.getSimpleName();

	private static final String UPDATE_JOB = TAG + ".UPDATE_JOB";

	public static void initJobManager(Application application) {
		JobManager.create(application).addJobCreator(tag -> {
			if (tag.equals(UPDATE_JOB)) {
				return new UpdateJob();
			}
			return null;
		});
	}

	public static void setupUpdateScheduling(Context context) {
		if (!Requirements.isFulfilled()) {
			cancelJobs();
		} else {
			int periodSeconds = context.getResources().getInteger(R.integer.scheduled_update_period_millis);
			int flexSeconds = context.getResources().getInteger(R.integer.scheduled_update_flex_millis);
			scheduleJob(periodSeconds, flexSeconds);
		}
	}

	private static void scheduleJob(int periodMillis, int flexMillis) {
		new JobRequest.Builder(UPDATE_JOB)
				.setPeriodic(periodMillis, flexMillis)
				.setRequiredNetworkType(JobRequest.NetworkType.NOT_ROAMING)
				.setRequirementsEnforced(true)
				.setPersisted(true)
				.setUpdateCurrent(true)
				.build()
				.schedule();
		Log.d(TAG, "Scheduling update to run periodically");
	}

	private static void cancelJobs() {
		JobManager.instance().cancelAllForTag(UPDATE_JOB);
	}

}
