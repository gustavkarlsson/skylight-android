package se.gustavkarlsson.skylight.android.background;

import android.util.Log;

import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;

import org.threeten.bp.Duration;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.Reusable;
import se.gustavkarlsson.skylight.android.settings.Settings;

import static se.gustavkarlsson.skylight.android.background.UpdateJob.UPDATE_JOB_TAG;
import static se.gustavkarlsson.skylight.android.dagger.Names.UPDATE_SCHEDULER_FLEX_NAME;
import static se.gustavkarlsson.skylight.android.dagger.Names.UPDATE_SCHEDULER_INTERVAL_NAME;

@Reusable
public class UpdateScheduler {
	private static final String TAG = UpdateScheduler.class.getSimpleName();

	private final Settings settings;
	private final Duration scheduleInterval;
	private final Duration scheduleFlex;

	@Inject
	UpdateScheduler(Settings settings, @Named(UPDATE_SCHEDULER_INTERVAL_NAME) Duration scheduleInterval, @Named(UPDATE_SCHEDULER_FLEX_NAME) Duration scheduleFlex) {
		this.settings = settings;
		this.scheduleInterval = scheduleInterval;
		this.scheduleFlex = scheduleFlex;
	}

	public void setupBackgroundUpdates() {
		boolean enabled = settings.isEnableNotifications();
		boolean jobScheduled = isScheduled();
		if (enabled && !jobScheduled) {
			scheduleJob();
		} else if (!enabled && jobScheduled) {
			cancelBackgroundUpdates();
		}
	}

	private static boolean isScheduled() {
		Set<JobRequest> jobRequests = JobManager.instance().getAllJobRequestsForTag(UPDATE_JOB_TAG);
		return !jobRequests.isEmpty();
	}

	private void scheduleJob() {
		new JobRequest.Builder(UPDATE_JOB_TAG)
				.setPeriodic(scheduleInterval.toMillis(), scheduleFlex.toMillis())
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
