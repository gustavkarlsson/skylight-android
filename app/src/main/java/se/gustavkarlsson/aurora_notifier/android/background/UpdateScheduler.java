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

	static final String UPDATE_NOW = TAG + ".UPDATE_NOW";
	static final String UPDATE_PERIODIC = TAG + ".UPDATE_PERIODIC";

	public static void initJobManager(Application application) {
		JobManager.create(application).addJobCreator(tag -> {
			if (tag.equals(UPDATE_NOW) || tag.equals(UPDATE_PERIODIC)) {
				return new UpdateJob();
			}
			return null;
		});
	}

	// TODO Don't use a boolean here
	public static void setupUpdateScheduling(Context context, boolean periodic) {
		if (!Requirements.isFulfilled()) {
			cancelPeriodic();
		} else if (periodic) {
			int periodSeconds = context.getResources().getInteger(R.integer.scheduled_update_period_seconds);
			int flexSeconds = context.getResources().getInteger(R.integer.scheduled_update_flex_seconds);
			schedulePeriodic(periodSeconds, flexSeconds);
		} else {
			scheduleNow();
		}
	}

	private static void schedulePeriodic(int periodSeconds, int flexSeconds) {
		new JobRequest.Builder(UPDATE_PERIODIC)
				.setPeriodic(periodSeconds * 1000, flexSeconds * 1000)
				.setRequiredNetworkType(JobRequest.NetworkType.NOT_ROAMING)
				.setRequirementsEnforced(true)
				.setPersisted(true)
				.setUpdateCurrent(true)
				.build()
				.schedule();
		Log.d(TAG, "Scheduling update to run periodically");
	}

	private static void cancelPeriodic() {
		JobManager.instance().cancelAllForTag(UPDATE_PERIODIC);
	}

	private static void scheduleNow() {
		new JobRequest.Builder(UPDATE_NOW)
				.setExact(1)
				.build()
				.schedule();
		Log.d(TAG, "Scheduling update to run NOW");
	}


}
