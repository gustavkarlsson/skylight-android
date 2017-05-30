package se.gustavkarlsson.aurora_notifier.android.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import javax.inject.Inject;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.cache.ReportNotificationCache;
import se.gustavkarlsson.aurora_notifier.android.evaluation.ChanceEvaluator;
import se.gustavkarlsson.aurora_notifier.android.gui.activities.main.MainActivity;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraReport;

public class NotificationHandler {
	private final ChanceEvaluator<AuroraReport> evaluator;
	private final Context context;
	private final NotificationManager notificationManager;
	private final ReportNotificationCache reportNotificationCache;
	private final NotificationDecider decider;

	@Inject
	public NotificationHandler(Context context, NotificationManager notificationManager, ReportNotificationCache reportNotificationCache, ChanceEvaluator<AuroraReport> evaluator, NotificationDecider decider) {
		this.evaluator = evaluator;
		this.context = context;
		this.notificationManager = notificationManager;
		this.reportNotificationCache = reportNotificationCache;
		this.decider = decider;
	}

	public void handle(AuroraReport report) {
		if (decider.shouldNotify(report)) {
			String text = evaluator.evaluate(report).toString();
			PendingIntent pendingIntent = createActivityPendingIntent();

			// FIXME improve notification
			Notification notification = new NotificationCompat.Builder(context)
					.setSmallIcon(R.drawable.common_google_signin_btn_text_light)
					.setContentTitle("Aurora!")
					.setContentText(text)
					.setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
					.setAutoCancel(true)
					.setPriority(NotificationCompat.PRIORITY_HIGH)
					.setDefaults(NotificationCompat.DEFAULT_ALL)
					.setContentIntent(pendingIntent)
					.build();

			notificationManager.notify(135551, notification);
		}
		reportNotificationCache.setLastNotified(report);
	}

	private PendingIntent createActivityPendingIntent() {
		Intent resultIntent = new Intent(context, MainActivity.class);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addParentStack(MainActivity.class);
		stackBuilder.addNextIntent(resultIntent);
		return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
	}
}
