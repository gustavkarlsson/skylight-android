package se.gustavkarlsson.aurora_notifier.android.notifications;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZoneOffset;

import javax.inject.Inject;

import se.gustavkarlsson.aurora_notifier.android.R;
import se.gustavkarlsson.aurora_notifier.android.cache.ReportNotificationCache;
import se.gustavkarlsson.aurora_notifier.android.evaluation.ChanceEvaluator;
import se.gustavkarlsson.aurora_notifier.android.evaluation.PresentableChance;
import se.gustavkarlsson.aurora_notifier.android.gui.activities.main.MainActivity;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraReport;

public class NotificationTracker {
	private final ChanceEvaluator<AuroraReport> evaluator;
	private final Context context;
	private final NotificationManager notificationManager;
	private final ReportNotificationCache reportNotificationCache;

	@Inject
	public NotificationTracker(Context context, NotificationManager notificationManager, ReportNotificationCache reportNotificationCache, ChanceEvaluator<AuroraReport> evaluator) {
		this.evaluator = evaluator;
		this.context = context;
		this.notificationManager = notificationManager;
		this.reportNotificationCache = reportNotificationCache;
	}

	public void reportReceived(AuroraReport report) {
		if (shouldNotify(report)) {
			sendNotification(report);
			reportNotificationCache.setLastNotified(report);
		}
	}

	private boolean shouldNotify(AuroraReport newReport) {
		AuroraReport lastReport = reportNotificationCache.getLastNotified();
		PresentableChance lastReportChance = lastReport == null ? PresentableChance.UNKNOWN : PresentableChance.fromChance(evaluator.evaluate(lastReport));
		PresentableChance newReportChance = PresentableChance.fromChance(evaluator.evaluate(newReport));
		return isHighEnoughChance(newReportChance) && isOutdated(lastReport) && isHigherThan(newReportChance, lastReportChance);
	}

	// FIXME use settings
	private static boolean isHighEnoughChance(PresentableChance chance) {
		return chance.ordinal() >= PresentableChance.MEDIUM.ordinal();
	}

	private static boolean isOutdated(AuroraReport report) {
		ZoneId currentZoneId = ZoneOffset.systemDefault();
		LocalDate now = LocalDate.now();
		Instant noonToday = LocalTime.NOON.atDate(now).atZone(currentZoneId).toInstant();
		Instant reportTime = Instant.ofEpochMilli(report.getTimestampMillis());
		return reportTime.isBefore(noonToday);
	}

	private static boolean isHigherThan(PresentableChance first, PresentableChance second) {
		return first.ordinal() > second.ordinal();
	}

	private void sendNotification(AuroraReport report) {
		PendingIntent pendingIntent = createActivityPendingIntent();
		Notification notification = new NotificationCompat.Builder(context)
				.setSmallIcon(R.drawable.common_google_signin_btn_text_light)
				.setContentTitle("Aurora!")
				.setContentText(report.toString())
				.setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
				.setAutoCancel(true)
				.setPriority(NotificationCompat.PRIORITY_HIGH)
				.setDefaults(NotificationCompat.DEFAULT_ALL)
				.setContentIntent(pendingIntent)
				.build();
		notificationManager.notify(135551, notification);
	}

	private PendingIntent createActivityPendingIntent() {
		Intent resultIntent = new Intent(context, MainActivity.class);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addParentStack(MainActivity.class);
		stackBuilder.addNextIntent(resultIntent);
		return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
	}
}
