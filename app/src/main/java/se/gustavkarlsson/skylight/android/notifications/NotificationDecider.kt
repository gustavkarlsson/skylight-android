package se.gustavkarlsson.skylight.android.notifications

import dagger.Reusable
import se.gustavkarlsson.skylight.android.cache.SingletonCache
import se.gustavkarlsson.skylight.android.dagger.Names.LAST_NOTIFIED_NAME
import se.gustavkarlsson.skylight.android.evaluation.ChanceEvaluator
import se.gustavkarlsson.skylight.android.evaluation.ChanceLevel
import se.gustavkarlsson.skylight.android.models.AuroraReport
import se.gustavkarlsson.skylight.android.settings.Settings
import javax.inject.Inject
import javax.inject.Named

@Reusable
class NotificationDecider
@Inject
internal constructor(
		@param:Named(LAST_NOTIFIED_NAME) private val lastNotifiedReportCache: SingletonCache<AuroraReport>,
		private val chanceEvaluator: ChanceEvaluator<AuroraReport>,
		private val settings: Settings,
		private val outdatedEvaluator: ReportOutdatedEvaluator
) {

    fun shouldNotify(newReport: AuroraReport): Boolean {
        if (!settings.isEnableNotifications) {
            return false
        }
        val lastReport = lastNotifiedReportCache.get()
        val newReportLevel = ChanceLevel.fromChance(chanceEvaluator.evaluate(newReport))
        if (lastReport == null) {
            return isHighEnoughChance(newReportLevel)
        }
        val lastReportLevel = ChanceLevel.fromChance(chanceEvaluator.evaluate(lastReport))
        return isHighEnoughChance(newReportLevel) && (outdatedEvaluator.isOutdated(lastReport) || isHigherThan(newReportLevel, lastReportLevel))
    }

	// TODO Combine with isHigherThan
    private fun isHighEnoughChance(chanceLevel: ChanceLevel): Boolean {
        val triggerLevel = settings.triggerLevel
        return chanceLevel.ordinal >= triggerLevel.ordinal
    }

	// TODO Move to ChanceLevel
    private fun isHigherThan(first: ChanceLevel, second: ChanceLevel): Boolean {
        return first.ordinal > second.ordinal
    }
}
