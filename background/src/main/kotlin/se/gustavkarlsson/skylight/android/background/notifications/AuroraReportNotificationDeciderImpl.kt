package se.gustavkarlsson.skylight.android.background.notifications

import se.gustavkarlsson.skylight.android.background.persistence.NotifiedRepository
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.entities.NotifiedChance
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.Settings

internal class AuroraReportNotificationDeciderImpl(
	private val notifiedRepository: NotifiedRepository,
	private val chanceEvaluator: ChanceEvaluator<AuroraReport>,
	private val settings: Settings,
	private val outdatedEvaluator: OutdatedEvaluator,
	private val appVisibilityEvaluator: AppVisibilityEvaluator
) : AuroraReportNotificationDecider {

	override fun shouldNotify(newReport: AuroraReport): Boolean {
		if (!settings.notificationsEnabled) return false
		if (appVisibilityEvaluator.isVisible()) return false
		val newChanceLevel = newReport.chanceLevel
		if (newChanceLevel < settings.triggerLevel) return false
		val lastNotified = notifiedRepository.get()
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

	override fun onNotified(notifiedReport: AuroraReport) {
		val chance = chanceEvaluator.evaluate(notifiedReport)
		val timestamp = notifiedReport.timestamp
		notifiedRepository.insert(NotifiedChance(chance, timestamp))
	}
}
