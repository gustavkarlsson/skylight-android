package se.gustavkarlsson.skylight.android.notifications;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZoneOffset;

import javax.inject.Inject;

import se.gustavkarlsson.skylight.android.cache.ReportNotificationCache;
import se.gustavkarlsson.skylight.android.evaluation.ChanceEvaluator;
import se.gustavkarlsson.skylight.android.evaluation.PresentableChance;
import se.gustavkarlsson.skylight.android.models.AuroraReport;
import se.gustavkarlsson.skylight.android.settings.Settings;

public class NotificationDecider {
	private final ReportNotificationCache reportNotificationCache;
	private final ChanceEvaluator<AuroraReport> evaluator;
	private final Settings settings;

	@Inject
	public NotificationDecider(ReportNotificationCache reportNotificationCache, ChanceEvaluator<AuroraReport> evaluator, Settings settings) {
		this.reportNotificationCache = reportNotificationCache;
		this.evaluator = evaluator;
		this.settings = settings;
	}

	boolean shouldNotify(AuroraReport newReport) {
		if (!settings.isEnableNotifications()) {
			return false;
		}
		AuroraReport lastReport = reportNotificationCache.getLastNotified();
		PresentableChance newReportChance = PresentableChance.fromChance(evaluator.evaluate(newReport));
		if (lastReport == null) {
			return isHighEnoughChance(newReportChance);
		}
		PresentableChance lastReportChance = PresentableChance.fromChance(evaluator.evaluate(lastReport));
		return isHighEnoughChance(newReportChance) && (isOutdated(lastReport) || isHigherThan(newReportChance, lastReportChance));
	}

	private boolean isHighEnoughChance(PresentableChance chance) {
		PresentableChance triggerLevel = settings.getTriggerLevel();
		return chance.ordinal() >= triggerLevel.ordinal();
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
