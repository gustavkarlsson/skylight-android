package se.gustavkarlsson.skylight.android.notifications

import dagger.Reusable
import se.gustavkarlsson.skylight.android.dagger.LAST_NOTIFIED_NAME
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.evaluation.ChanceEvaluator
import se.gustavkarlsson.skylight.android.evaluation.ChanceLevel
import se.gustavkarlsson.skylight.android.services.SingletonCache
import se.gustavkarlsson.skylight.android.settings.Settings
import javax.inject.Inject
import javax.inject.Named

@Reusable
class NotificationDecider
@Inject
constructor(
	@param:Named(LAST_NOTIFIED_NAME) private val lastNotifiedReportCache: SingletonCache<AuroraReport>,
	private val chanceEvaluator: ChanceEvaluator<AuroraReport>,
	private val settings: Settings,
	private val outdatedEvaluator: ReportOutdatedEvaluator
) {

    fun shouldNotify(newReport: AuroraReport): Boolean {
        if (!settings.isEnableNotifications) return false
		val newReportLevel = ChanceLevel.fromChance(chanceEvaluator.evaluate(newReport))
		if (newReportLevel < settings.triggerLevel) return false
		val lastReport = lastNotifiedReportCache.value
		val lastReportLevel = ChanceLevel.fromChance(chanceEvaluator.evaluate(lastReport))
        return lastReport.outdated || newReportLevel > lastReportLevel
    }

	private val AuroraReport.outdated: Boolean
		get() = outdatedEvaluator.isOutdated(this)
}
