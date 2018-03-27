package se.gustavkarlsson.skylight.android.services_impl.notifications

import dagger.Reusable
import se.gustavkarlsson.skylight.android.dagger.LAST_NOTIFIED_NAME
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.Settings
import se.gustavkarlsson.skylight.android.services.SingletonCache
import se.gustavkarlsson.skylight.android.services_impl.AppVisibilityEvaluator
import javax.inject.Inject
import javax.inject.Named

@Reusable
class AuroraReportNotificationDecider
@Inject
constructor(
	@param:Named(LAST_NOTIFIED_NAME) private val lastNotifiedReportCache: SingletonCache<AuroraReport>,
	private val chanceEvaluator: ChanceEvaluator<AuroraReport>,
	private val settings: Settings,
	private val outdatedEvaluator: ReportOutdatedEvaluator,
	private val appVisibilityEvaluator: AppVisibilityEvaluator
) {

    fun shouldNotify(newReport: AuroraReport): Boolean {
        if (!settings.notificationsEnabled) return false
		if (appVisibilityEvaluator.isVisible()) return false
		val newReportLevel = newReport.chanceLevel
		if (newReportLevel < settings.triggerLevel) return false
		val lastReport = lastNotifiedReportCache.value
		if (lastReport.outdated) return true
		val lastReportLevel = lastReport.chanceLevel
        return newReportLevel > lastReportLevel
    }

	private val AuroraReport.chanceLevel: ChanceLevel
		get() = ChanceLevel.fromChance(chanceEvaluator.evaluate(this))

	private val AuroraReport.outdated: Boolean
		get() = outdatedEvaluator.isOutdated(this)

    fun onNotified(notifiedReport: AuroraReport) {
        lastNotifiedReportCache.value = notifiedReport
    }
}
