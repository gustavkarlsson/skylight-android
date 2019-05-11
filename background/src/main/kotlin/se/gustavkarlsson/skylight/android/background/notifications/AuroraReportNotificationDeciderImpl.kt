package se.gustavkarlsson.skylight.android.background.notifications

import se.gustavkarlsson.skylight.android.background.persistence.NotifiedChanceRepository
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.entities.NotifiedChance
import se.gustavkarlsson.skylight.android.krate.SkylightStore
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator

internal class AuroraReportNotificationDeciderImpl(
	private val notifiedChanceRepository: NotifiedChanceRepository,
	private val chanceEvaluator: ChanceEvaluator<AuroraReport>,
	private val store: SkylightStore,
	private val outdatedEvaluator: OutdatedEvaluator,
	private val appVisibilityEvaluator: AppVisibilityEvaluator
) : AuroraReportNotificationDecider {

	override fun shouldNotify(newReport: AuroraReport): Boolean {
		val notificationsEnabled = true // FIXME get from settings
		val triggerLevel = ChanceLevel.MEDIUM // FIXME get from settings
		if (!notificationsEnabled) return false
		if (appVisibilityEvaluator.isVisible()) return false
		val newChanceLevel = newReport.chanceLevel
		if (newChanceLevel < triggerLevel) return false
		val lastNotified = notifiedChanceRepository.get()
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
		notifiedChanceRepository.insert(NotifiedChance(chance, timestamp))
	}
}
