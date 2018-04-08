package se.gustavkarlsson.skylight.android

import se.gustavkarlsson.skylight.android.entities.Chance
import se.gustavkarlsson.skylight.android.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.services.Analytics

private var enabled = true

fun disableAnalytics() {
	enabled = false
}

val analytics: Analytics
	get() {
		return if (enabled) {
			appComponent.analytics
		} else {
			object : Analytics {
				override fun logNotificationSent(chance: Chance) = Unit
				override fun logManualRefresh() = Unit
				override fun setNotificationsEnabled(enabled: Boolean) = Unit
				override fun setNotifyTriggerLevel(triggerLevel: ChanceLevel) = Unit
			}
		}
	}
