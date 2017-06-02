package se.gustavkarlsson.skylight.android.background;

import android.content.Context;
import android.util.Log;

import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;

import java.util.Set;

import javax.inject.Inject;

import dagger.Reusable;
import se.gustavkarlsson.skylight.android.R;
import se.gustavkarlsson.skylight.android.settings.Settings;

import static se.gustavkarlsson.skylight.android.background.UpdateJob.UPDATE_JOB_TAG;

@Reusable
public class UpdateScheduler {
	private static final String TAG = UpdateScheduler.class.getSimpleName();

	private final Settings settings;
	private final int intervalMillis;
	private final int flexMillis;

	@Inject
	UpdateScheduler(Context context, Settings settings) {
		this.settings = settings;
		intervalMillis = context.getResources().getInteger(R.integer.setting_scheduled_update_interval_millis);
		flexMillis = context.getResources().getInteger(R.integer.setting_scheduled_update_flex_millis);
	}

	public void setupBackgroundUpdates() {
		boolean enabled = settings.isEnableNotifications();
		boolean jobScheduled = isScheduled();
		if (enabled && !jobScheduled) {
			scheduleJob(intervalMillis, flexMillis);
		} else if (!enabled && jobScheduled) {
			cancelBackgroundUpdates();
		}
	}

	private static boolean isScheduled() {
		Set<JobRequest> jobRequests = JobManager.instance().getAllJobRequestsForTag(UPDATE_JOB_TAG);
		return !jobRequests.isEmpty();
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
		Log.d(TAG, "Scheduling update to run periodically");
	}

	void cancelBackgroundUpdates() {
		JobManager.instance().cancelAllForTag(UPDATE_JOB_TAG);
	}

}
