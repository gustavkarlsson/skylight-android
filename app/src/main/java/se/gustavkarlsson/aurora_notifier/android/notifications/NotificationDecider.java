package se.gustavkarlsson.aurora_notifier.android.notifications;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZoneOffset;

import javax.inject.Inject;

import se.gustavkarlsson.aurora_notifier.android.cache.ReportNotificationCache;
import se.gustavkarlsson.aurora_notifier.android.evaluation.ChanceEvaluator;
import se.gustavkarlsson.aurora_notifier.android.evaluation.PresentableChance;
import se.gustavkarlsson.aurora_notifier.android.models.AuroraReport;

public class NotificationDecider {
	private final ReportNotificationCache reportNotificationCache;
	private final ChanceEvaluator<AuroraReport> evaluator;

	@Inject
	public NotificationDecider(ReportNotificationCache reportNotificationCache, ChanceEvaluator<AuroraReport> evaluator) {
		this.reportNotificationCache = reportNotificationCache;
		this.evaluator = evaluator;
	}

	boolean shouldNotify(AuroraReport newReport) {
		AuroraReport lastReport = reportNotificationCache.getLastNotified();
		PresentableChance newReportChance = PresentableChance.fromChance(evaluator.evaluate(newReport));
		if (lastReport == null) {
			return isHighEnoughChance(newReportChance);
		}
		PresentableChance lastReportChance = PresentableChance.fromChance(evaluator.evaluate(lastReport));
		return isHighEnoughChance(newReportChance) && (isOutdated(lastReport) || isHigherThan(newReportChance, lastReportChance));
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
}
