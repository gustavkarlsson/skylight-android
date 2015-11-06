package se.gustavkarlsson.aurora_notifier.android.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v7.app.NotificationCompat;

import se.gustavkarlsson.aurora_notifier.common.domain.Timestamped;

public class AuroraNotificationSender implements NotificationSender<Timestamped<Float>> {

	public static final int NOTIFICATION_ID_PERFECT_CONDITIONS = 0;

	private final Context context;


	public AuroraNotificationSender(Context context) {
		this.context = context;
	}

	@Override
	public void notify(Timestamped<Float> kpIndex) {
		Notification notification = new NotificationCompat.Builder(context)
				.setSmallIcon(android.R.drawable.ic_dialog_alert)
				.setContentTitle("KP Index updated")
				.setContentText(kpIndex.toString()).build();

		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(NOTIFICATION_ID_PERFECT_CONDITIONS, notification);
	}
}
