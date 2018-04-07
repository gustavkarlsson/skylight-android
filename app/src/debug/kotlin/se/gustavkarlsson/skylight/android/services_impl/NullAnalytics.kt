package se.gustavkarlsson.skylight.android.services_impl

import se.gustavkarlsson.skylight.android.entities.Chance
import se.gustavkarlsson.skylight.android.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.services.Analytics

class NullAnalytics : Analytics {
	override fun logNotificationSent(chance: Chance) = Unit

	override fun logManualRefresh() = Unit

	override fun setNotificationsEnabled(enabled: Boolean) = Unit

	override fun setNotifyTriggerLevel(triggerLevel: ChanceLevel) = Unit
}
