package se.gustavkarlsson.skylight.android.services

import android.app.Activity
import se.gustavkarlsson.skylight.android.entities.Chance
import se.gustavkarlsson.skylight.android.entities.ChanceLevel

interface Analytics {
	fun logNotificationSent(chance: Chance)
	fun logScreen(activity: Activity, name: String)

	fun setNotificationsEnabled(enabled: Boolean)
	fun setNotifyTriggerLevel(triggerLevel: ChanceLevel)
}
