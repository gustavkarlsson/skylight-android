package se.gustavkarlsson.aurora_notifier.android.background;

import android.content.Context;
import android.util.Log;

import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.settings.Settings;

import static se.gustavkarlsson.aurora_notifier.android.background.UpdateJob.UPDATE_JOB_TAG;

public class UpdateScheduler {
	private static final String TAG = UpdateScheduler.class.getSimpleName();

	private final Settings settings;
	private final int intervalMillis;
	private final int flexMillis;
	private boolean jobScheduled = false;

	public UpdateScheduler(Context context, Settings settings) {
		this.settings = settings;
		intervalMillis = context.getResources().getInteger(R.integer.setting_scheduled_update_interval_millis);
		flexMillis = context.getResources().getInteger(R.integer.setting_scheduled_update_flex_millis);
	}

	public void setupBackgroundUpdates() {
		boolean enabled = settings.isEnableNotifications();
		if (enabled && !jobScheduled) {
			scheduleJob(intervalMillis, flexMillis);
		} else if (!enabled && jobScheduled) {
			cancelBackgroundUpdates();
		}
	}

	private void scheduleJob(int intervalMillis, int flexMillis) {
		new JobRequest.Builder(UPDATE_JOB_TAG)
				.setPeriodic(intervalMillis, flexMillis)
				.setPersisted(true)
				.setUpdateCurrent(true)
				.setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
				.setRequirementsEnforced(true)
				.build()
				.schedule();
		jobScheduled = true;
		Log.d(TAG, "Scheduling update to run periodically");
	}

	void cancelBackgroundUpdates() {
		JobManager.instance().cancelAllForTag(UPDATE_JOB_TAG);
		jobScheduled = false;
	}

}
