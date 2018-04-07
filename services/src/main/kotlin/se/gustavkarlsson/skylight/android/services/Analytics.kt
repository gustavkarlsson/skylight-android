package se.gustavkarlsson.skylight.android.services

import se.gustavkarlsson.skylight.android.entities.Chance
import se.gustavkarlsson.skylight.android.entities.ChanceLevel

interface Analytics {
	fun logNotificationSent(chance: Chance)
	fun logManualRefresh()

	fun setNotificationsEnabled(enabled: Boolean)
	fun setNotifyThreshold(chanceLevel: ChanceLevel)
}
