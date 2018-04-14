package se.gustavkarlsson.skylight.android.services

import se.gustavkarlsson.skylight.android.entities.Chance
import se.gustavkarlsson.skylight.android.entities.ChanceLevel

interface Analytics {
	fun logNotificationSent(chance: Chance)
	fun logManualRefresh()

	fun setNotificationsEnabled(enabled: Boolean)
	fun setNotifyTriggerLevel(triggerLevel: ChanceLevel)

	companion object : Analytics {

		var instance: Analytics = NullAnalytics()

		override fun logNotificationSent(chance: Chance) = instance.logNotificationSent(chance)
		override fun logManualRefresh() = instance.logManualRefresh()
		override fun setNotificationsEnabled(enabled: Boolean) = instance.setNotificationsEnabled(enabled)
		override fun setNotifyTriggerLevel(triggerLevel: ChanceLevel) = instance.setNotifyTriggerLevel(triggerLevel)
	}

	class NullAnalytics : Analytics {
		override fun logNotificationSent(chance: Chance) = Unit
		override fun logManualRefresh() = Unit
		override fun setNotificationsEnabled(enabled: Boolean) = Unit
		override fun setNotifyTriggerLevel(triggerLevel: ChanceLevel) = Unit
	}
}
