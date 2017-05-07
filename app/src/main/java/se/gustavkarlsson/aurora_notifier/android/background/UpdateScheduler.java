package se.gustavkarlsson.aurora_notifier.android.background;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;

import se.gustavkarlsson.aurora_notifier.android.R;

public class UpdateScheduler {
	private static final String TAG = UpdateScheduler.class.getSimpleName();

	private static final String UPDATE_JOB = TAG + ".UPDATE_JOB";

	private static boolean scheduled = false;

	public static void initJobManager(Application application) {
		JobManager.create(application).addJobCreator(tag -> {
			if (tag.equals(UPDATE_JOB)) {
				return new UpdateJob();
			}
			return null;
		});
	}

	public static void setupUpdateScheduling(Context context) {
		String key = context.getString(R.string.pref_notifications_key);
		SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		boolean enabled = sharedPreferences.getBoolean(key, true);
		if (enabled && !scheduled) {
			int intervalMillis = context.getResources().getInteger(R.integer.scheduled_update_interval_millis);
			int flexMillis = context.getResources().getInteger(R.integer.scheduled_update_flex_millis);
			scheduleJob(intervalMillis, flexMillis);
		} else if (!enabled && scheduled) {
			cancelJobs();
		}
	}

	private static void scheduleJob(int intervalMillis, int flexMillis) {
		new JobRequest.Builder(UPDATE_JOB)
				.setPeriodic(intervalMillis, flexMillis)
				.setPersisted(true)
				.setUpdateCurrent(true)
				.setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
				.setRequirementsEnforced(true)
				.build()
				.schedule();
		scheduled = true;
		Log.d(TAG, "Scheduling update to run periodically");
	}

	static void cancelJobs() {
		JobManager.instance().cancelAllForTag(UPDATE_JOB);
		scheduled = false;
	}

}
