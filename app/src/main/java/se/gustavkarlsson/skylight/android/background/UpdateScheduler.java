package se.gustavkarlsson.skylight.android.background;

import android.util.Log;

import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;

import org.threeten.bp.Duration;

import java.util.Set;

import javax.inject.Inject;

import dagger.Reusable;
import se.gustavkarlsson.skylight.android.settings.Settings;

import static se.gustavkarlsson.skylight.android.background.UpdateJob.UPDATE_JOB_TAG;

@Reusable
public class UpdateScheduler {
	private static final String TAG = UpdateScheduler.class.getSimpleName();

	private static final Duration INTERVAL = Duration.ofMinutes(20);
	private static final Duration FLEX = Duration.ofMinutes(10);

	private final Settings settings;

	@Inject
	UpdateScheduler(Settings settings) {
		this.settings = settings;
	}

	public void setupBackgroundUpdates() {
		boolean enabled = settings.isEnableNotifications();
		boolean jobScheduled = isScheduled();
		if (enabled && !jobScheduled) {
			scheduleJob(INTERVAL, FLEX);
		} else if (!enabled && jobScheduled) {
			cancelBackgroundUpdates();
		}
	}

	private static boolean isScheduled() {
		Set<JobRequest> jobRequests = JobManager.instance().getAllJobRequestsForTag(UPDATE_JOB_TAG);
		return !jobRequests.isEmpty();
	}

	private void scheduleJob(Duration interval, Duration flex) {
		new JobRequest.Builder(UPDATE_JOB_TAG)
				.setPeriodic(interval.toMillis(), flex.toMillis())
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
