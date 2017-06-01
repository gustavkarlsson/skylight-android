package se.gustavkarlsson.skylight.android.background;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.evernote.android.job.Job;

import javax.inject.Inject;

import se.gustavkarlsson.skylight.android.R;
import se.gustavkarlsson.skylight.android.gui.activities.AuroraRequirementsCheckingActivity;

import static com.evernote.android.job.Job.Result.FAILURE;
import static com.evernote.android.job.Job.Result.SUCCESS;

public class UpdateJob extends Job {
	private static final String TAG = UpdateJob.class.getSimpleName();

	public static final String UPDATE_JOB_TAG = TAG + ".UPDATE_JOB";

	private final NotificationManager notificationManager;
	private final UpdateScheduler updateScheduler;
	private final Updater updater;

	@Inject
	UpdateJob(NotificationManager notificationManager, UpdateScheduler updateScheduler, Updater updater) {
		this.notificationManager = notificationManager;
		this.updateScheduler = updateScheduler;
		this.updater = updater;
	}

	@NonNull
	@Override
	protected Result onRunJob(Params params) {
		int updateTimeoutMillis = getContext().getResources().getInteger(R.integer.setting_background_update_timeout_millis);
		if (!hasLocationPermission()) {
			updateScheduler.cancelBackgroundUpdates();
			sendLocationPermissionMissingNotification(notificationManager);
			return FAILURE;
		}
		boolean successful = updater.update(updateTimeoutMillis);
		return successful ? SUCCESS : FAILURE;
	}

	private boolean hasLocationPermission() {
		return ContextCompat.checkSelfPermission(getContext(), AuroraRequirementsCheckingActivity.LOCATION_PERMISSION) == PackageManager.PERMISSION_GRANTED;
	}

	// FIXME Improve notification
	private void sendLocationPermissionMissingNotification(NotificationManager notificationManager) {
		Context context = getContext();

		Notification notification = new NotificationCompat.Builder(context)
				.setSmallIcon(R.drawable.common_google_signin_btn_text_light)
				.setContentTitle(context.getString(R.string.error_aurora_notifications_disabled_title))
				.setContentText(context.getString(R.string.error_aurora_notifications_disabled_content))
				.setCategory(NotificationCompat.CATEGORY_ERROR)
				.setAutoCancel(true)
				.setPriority(NotificationCompat.PRIORITY_HIGH)
				.setDefaults(NotificationCompat.DEFAULT_ALL)
				.build();

		notificationManager.notify(24656, notification);
	}
}
