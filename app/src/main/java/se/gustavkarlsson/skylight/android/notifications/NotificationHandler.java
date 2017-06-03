package se.gustavkarlsson.skylight.android.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.Reusable;
import se.gustavkarlsson.skylight.android.R;
import se.gustavkarlsson.skylight.android.cache.SingletonCache;
import se.gustavkarlsson.skylight.android.evaluation.ChanceEvaluator;
import se.gustavkarlsson.skylight.android.gui.activities.main.MainActivity;
import se.gustavkarlsson.skylight.android.models.AuroraReport;

import static se.gustavkarlsson.skylight.android.dagger.modules.replaceable.AuroraReportModule.LAST_NOTIFIED_NAME;

@Reusable
public class NotificationHandler {
	private final ChanceEvaluator<AuroraReport> evaluator;
	private final Context context;
	private final NotificationManager notificationManager;
	private final SingletonCache<AuroraReport> lastNotifiedReportCache;
	private final NotificationDecider decider;

	@Inject
	NotificationHandler(Context context, NotificationManager notificationManager, @Named(LAST_NOTIFIED_NAME) SingletonCache<AuroraReport> lastNotifiedReportCache, ChanceEvaluator<AuroraReport> evaluator, NotificationDecider decider) {
		this.evaluator = evaluator;
		this.context = context;
		this.notificationManager = notificationManager;
		this.lastNotifiedReportCache = lastNotifiedReportCache;
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
		lastNotifiedReportCache.set(report);
	}

	private PendingIntent createActivityPendingIntent() {
		Intent resultIntent = new Intent(context, MainActivity.class);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addParentStack(MainActivity.class);
		stackBuilder.addNextIntent(resultIntent);
		return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
	}
}
