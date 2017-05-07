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

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.gui.activities.main.MainActivity;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.evernote.android.job.Job.Result.FAILURE;
import static com.evernote.android.job.Job.Result.SUCCESS;
import static se.gustavkarlsson.aurora_notifier.android.gui.activities.AuroraRequirementsCheckingActivity.LOCATION_PERMISSION;

class UpdateJob extends Job {

	@NonNull
	@Override
	protected Result onRunJob(Params params) {
		if (!requirementsMet()) {
			UpdateScheduler.cancelJobs();
			sendErrorNotification();
			return FAILURE;
		}
		int timeoutMillis = getContext().getResources().getInteger(R.integer.background_update_timeout_millis);
		Updater updater = new Updater(getContext(), timeoutMillis);
		boolean successful = updater.update();
		return successful ? SUCCESS : FAILURE;
	}

	private boolean requirementsMet() {
		boolean googlePlayServicesAvailable = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(getContext()) == ConnectionResult.SUCCESS;
		boolean hasLocationPermission = ContextCompat.checkSelfPermission(getContext(), LOCATION_PERMISSION) == PackageManager.PERMISSION_GRANTED;
		return googlePlayServicesAvailable && hasLocationPermission;
	}

	// TODO improve notification
	private void sendErrorNotification() {
		Context context = getContext();
		Intent intent = new Intent(context, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 0, intent, 0);

		Notification notification = new NotificationCompat.Builder(context)
				.setSmallIcon(R.drawable.ic_error_white_24dp)
				.setContentTitle(context.getString(R.string.error_aurora_notifications_not_possible_title))
				.setContentText(context.getString(R.string.error_aurora_notifications_not_possible_touch_to_fix))
				.setContentIntent(contentIntent)
				.build();
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
		// TODO handle ID
		notificationManager.notify((int) (Math.random() * 1000), notification);
	}
}
