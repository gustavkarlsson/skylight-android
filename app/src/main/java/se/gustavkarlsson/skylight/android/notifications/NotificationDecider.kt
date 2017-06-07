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
internal class NotificationDecider
@Inject
constructor(
		@param:Named(LAST_NOTIFIED_NAME) private val lastNotifiedReportCache: SingletonCache<AuroraReport>,
		private val chanceEvaluator: ChanceEvaluator<AuroraReport>,
		private val settings: Settings,
		private val outdatedEvaluator: ReportOutdatedEvaluator
) {

    fun shouldNotify(newReport: AuroraReport): Boolean {
        if (!settings.isEnableNotifications) return false
        val lastReport = lastNotifiedReportCache.value
        val newReportLevel = ChanceLevel.fromChance(chanceEvaluator.evaluate(newReport))
        val lastReportLevel = ChanceLevel.fromChance(chanceEvaluator.evaluate(lastReport))
        return newReportLevel >= settings.triggerLevel && (outdatedEvaluator.isOutdated(lastReport) || newReportLevel > lastReportLevel)
    }
}
