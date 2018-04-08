package se.gustavkarlsson.skylight.android.services_impl.notifications

import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.entities.NotifiedChance
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.LastNotifiedChanceRepository
import se.gustavkarlsson.skylight.android.services.Settings
import se.gustavkarlsson.skylight.android.services_impl.AppVisibilityEvaluator

class AuroraReportNotificationDecider(
	private val lastNotifiedChanceRepository: LastNotifiedChanceRepository,
	private val chanceEvaluator: ChanceEvaluator<AuroraReport>,
	private val settings: Settings,
	private val outdatedEvaluator: OutdatedEvaluator,
	private val appVisibilityEvaluator: AppVisibilityEvaluator
) {

    fun shouldNotify(newReport: AuroraReport): Boolean {
        if (!settings.notificationsEnabled) return false
		if (appVisibilityEvaluator.isVisible()) return false
		val newChanceLevel = newReport.chanceLevel
		if (newChanceLevel < settings.triggerLevel) return false
		val lastNotified = lastNotifiedChanceRepository.get()
		if (lastNotified.outdated) return true
		val lastChanceLevel = lastNotified.chanceLevel
        return newChanceLevel > lastChanceLevel
    }

	private val AuroraReport.chanceLevel: ChanceLevel
		get() = ChanceLevel.fromChance(chanceEvaluator.evaluate(this))

	private val NotifiedChance?.chanceLevel: ChanceLevel
		get() = this?.chance?.let { ChanceLevel.fromChance(it) } ?: ChanceLevel.UNKNOWN

	private val NotifiedChance?.outdated: Boolean
		get() = this?.timestamp?.let { outdatedEvaluator.isOutdated(it) } ?: true

    fun onNotified(notifiedReport: AuroraReport) {
		val chance = chanceEvaluator.evaluate(notifiedReport)
		val timestamp = notifiedReport.timestamp
		lastNotifiedChanceRepository.insert(NotifiedChance(chance, timestamp))
    }
}
