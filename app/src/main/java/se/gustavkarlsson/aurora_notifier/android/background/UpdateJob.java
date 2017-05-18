package se.gustavkarlsson.aurora_notifier.android.background;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.evernote.android.job.Job;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.Random;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.dagger.components.DaggerUpdateJobComponent;
import se.gustavkarlsson.aurora_notifier.android.dagger.components.UpdateJobComponent;
import se.gustavkarlsson.aurora_notifier.android.gui.activities.main.MainActivity;

import static com.evernote.android.job.Job.Result.FAILURE;
import static com.evernote.android.job.Job.Result.SUCCESS;
import static se.gustavkarlsson.aurora_notifier.android.AuroraNotifier.getApplicationComponent;
import static se.gustavkarlsson.aurora_notifier.android.gui.activities.AuroraRequirementsCheckingActivity.LOCATION_PERMISSION;

public class UpdateJob extends Job {
	private static final String TAG = UpdateJob.class.getSimpleName();

	public static final String UPDATE_JOB_TAG = TAG + ".UPDATE_JOB";

	@NonNull
	@Override
	protected Result onRunJob(Params params) {
		UpdateJobComponent component = DaggerUpdateJobComponent.builder()
				.applicationComponent(getApplicationComponent(getContext()))
				.build();
		NotificationManager notificationManager = component.getNotificationManager();
		UpdateScheduler updateScheduler = component.getUpdateScheduler();
		Updater updater = component.getUpdater();
		int updateTimeoutMillis = getContext().getResources().getInteger(R.integer.setting_background_update_timeout_millis);

		if (!requirementsMet()) {
			updateScheduler.cancelBackgroundUpdates();
			sendErrorNotification(notificationManager);
			return FAILURE;
		}
		boolean successful = updater.update(updateTimeoutMillis);
		return successful ? SUCCESS : FAILURE;
	}

	private boolean requirementsMet() {
		boolean googlePlayServicesAvailable = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext()) == ConnectionResult.SUCCESS;
		boolean hasLocationPermission = ContextCompat.checkSelfPermission(getContext(), LOCATION_PERMISSION) == PackageManager.PERMISSION_GRANTED;
		return googlePlayServicesAvailable && hasLocationPermission;
	}

	// TODO improve notification (flags can make two MainActivity stack)
	private void sendErrorNotification(NotificationManager notificationManager) {
		Context context = getContext();
		Intent intent = new Intent(context, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);

		Notification notification = new NotificationCompat.Builder(context)
				.setContentTitle(context.getString(R.string.error_aurora_notifications_not_possible_title))
				.setContentText(context.getString(R.string.error_aurora_notifications_not_possible_touch_to_fix))
				.setContentIntent(contentIntent)
				.build();
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		// TODO handle ID
		notificationManager.notify(new Random().nextInt(1000), notification);
	}
}
