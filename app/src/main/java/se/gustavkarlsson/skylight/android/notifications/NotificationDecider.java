package se.gustavkarlsson.skylight.android.notifications;

import javax.inject.Inject;

import se.gustavkarlsson.skylight.android.cache.ReportNotificationCache;
import se.gustavkarlsson.skylight.android.evaluation.ChanceEvaluator;
import se.gustavkarlsson.skylight.android.evaluation.PresentableChance;
import se.gustavkarlsson.skylight.android.models.AuroraReport;
import se.gustavkarlsson.skylight.android.settings.Settings;

public class NotificationDecider {
	private final ReportNotificationCache reportNotificationCache;
	private final ChanceEvaluator<AuroraReport> chanceEvaluator;
	private final Settings settings;
	private final ReportOutdatedEvaluator outdatedEvaluator;

	@Inject
	public NotificationDecider(ReportNotificationCache reportNotificationCache, ChanceEvaluator<AuroraReport> chanceEvaluator, Settings settings, ReportOutdatedEvaluator outdatedEvaluator) {
		this.reportNotificationCache = reportNotificationCache;
		this.chanceEvaluator = chanceEvaluator;
		this.settings = settings;
		this.outdatedEvaluator = outdatedEvaluator;
	}

	boolean shouldNotify(AuroraReport newReport) {
		if (!settings.isEnableNotifications()) {
			return false;
		}
		AuroraReport lastReport = reportNotificationCache.getLastNotified();
		PresentableChance newReportChance = PresentableChance.fromChance(chanceEvaluator.evaluate(newReport));
		if (lastReport == null) {
			return isHighEnoughChance(newReportChance);
		}
		PresentableChance lastReportChance = PresentableChance.fromChance(chanceEvaluator.evaluate(lastReport));
		return isHighEnoughChance(newReportChance) && (outdatedEvaluator.isOutdated(lastReport) || isHigherThan(newReportChance, lastReportChance));
	}

	private boolean isHighEnoughChance(PresentableChance chance) {
		PresentableChance triggerLevel = settings.getTriggerLevel();
		return chance.ordinal() >= triggerLevel.ordinal();
	}

	private static boolean isHigherThan(PresentableChance first, PresentableChance second) {
		return first.ordinal() > second.ordinal();
	}
}
