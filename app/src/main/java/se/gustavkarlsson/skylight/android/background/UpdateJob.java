package se.gustavkarlsson.skylight.android.background;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.evernote.android.job.Job;

import org.threeten.bp.Duration;

import javax.inject.Inject;
import javax.inject.Named;

import se.gustavkarlsson.skylight.android.R;
import se.gustavkarlsson.skylight.android.gui.activities.AuroraRequirementsCheckingActivity;

import static com.evernote.android.job.Job.Result.FAILURE;
import static com.evernote.android.job.Job.Result.SUCCESS;
import static se.gustavkarlsson.skylight.android.dagger.Names.BACKGROUND_UPDATE_TIMEOUT_NAME;

public class UpdateJob extends Job {
	private static final String TAG = UpdateJob.class.getSimpleName();

	public static final String UPDATE_JOB_TAG = TAG + ".UPDATE_JOB";

	private final NotificationManager notificationManager;
	private final UpdateScheduler updateScheduler;
	private final Updater updater;
	private final Duration timeout;

	@Inject
	public UpdateJob(NotificationManager notificationManager, UpdateScheduler updateScheduler, Updater updater, @Named(BACKGROUND_UPDATE_TIMEOUT_NAME) Duration timeout) {
		this.notificationManager = notificationManager;
		this.updateScheduler = updateScheduler;
		this.updater = updater;
		this.timeout = timeout;
	}

	@NonNull
	@Override
	protected Result onRunJob(Params params) {
		if (!hasLocationPermission()) {
			updateScheduler.cancelBackgroundUpdates();
			sendLocationPermissionMissingNotification(notificationManager);
			return FAILURE;
		}
		boolean successful = updater.update(timeout.toMillis());
		return successful ? SUCCESS : FAILURE;
	}

	private boolean hasLocationPermission() {
		return ContextCompat.checkSelfPermission(getContext(), AuroraRequirementsCheckingActivity.LOCATION_PERMISSION) == PackageManager.PERMISSION_GRANTED;
	}

	// TODO Add better handling
	private void sendLocationPermissionMissingNotification(NotificationManager notificationManager) {
		Context context = getContext();

		Notification notification = new NotificationCompat.Builder(context)
				.setSmallIcon(R.drawable.app_logo_small)
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
