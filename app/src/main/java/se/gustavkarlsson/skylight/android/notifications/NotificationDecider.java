package se.gustavkarlsson.skylight.android.notifications;

import javax.inject.Inject;
import javax.inject.Named;

import dagger.Reusable;
import se.gustavkarlsson.skylight.android.cache.SingletonCache;
import se.gustavkarlsson.skylight.android.evaluation.ChanceEvaluator;
import se.gustavkarlsson.skylight.android.evaluation.ChanceLevel;
import se.gustavkarlsson.skylight.android.models.AuroraReport;
import se.gustavkarlsson.skylight.android.settings.Settings;

import static se.gustavkarlsson.skylight.android.dagger.modules.replaceable.AuroraReportModule.LAST_NOTIFIED_NAME;

@Reusable
public class NotificationDecider {
	private final SingletonCache<AuroraReport> lastNotifiedReportCache;
	private final ChanceEvaluator<AuroraReport> chanceEvaluator;
	private final Settings settings;
	private final ReportOutdatedEvaluator outdatedEvaluator;

	@Inject
	NotificationDecider(@Named(LAST_NOTIFIED_NAME) SingletonCache<AuroraReport> lastNotifiedReportCache, ChanceEvaluator<AuroraReport> chanceEvaluator, Settings settings, ReportOutdatedEvaluator outdatedEvaluator) {
		this.lastNotifiedReportCache = lastNotifiedReportCache;
		this.chanceEvaluator = chanceEvaluator;
		this.settings = settings;
		this.outdatedEvaluator = outdatedEvaluator;
	}

	boolean shouldNotify(AuroraReport newReport) {
		if (!settings.isEnableNotifications()) {
			return false;
		}
		AuroraReport lastReport = lastNotifiedReportCache.get();
		ChanceLevel newReportLevel = ChanceLevel.fromChance(chanceEvaluator.evaluate(newReport));
		if (lastReport == null) {
			return isHighEnoughChance(newReportLevel);
		}
		ChanceLevel lastReportLevel = ChanceLevel.fromChance(chanceEvaluator.evaluate(lastReport));
		return isHighEnoughChance(newReportLevel) && (outdatedEvaluator.isOutdated(lastReport) || isHigherThan(newReportLevel, lastReportLevel));
	}

	private boolean isHighEnoughChance(ChanceLevel chanceLevel) {
		ChanceLevel triggerLevel = settings.getTriggerLevel();
		return chanceLevel.ordinal() >= triggerLevel.ordinal();
	}

	private static boolean isHigherThan(ChanceLevel first, ChanceLevel second) {
		return first.ordinal() > second.ordinal();
	}
}
