package se.gustavkarlsson.skylight.android.services_impl

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import se.gustavkarlsson.skylight.android.entities.Chance
import se.gustavkarlsson.skylight.android.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.services.Analytics

class FirebasedAnalytics(context: Context) : Analytics {
	private val firebaseAnalytics = FirebaseAnalytics.getInstance(context)

	override fun logNotificationSent(chance: Chance) {
		firebaseAnalytics.logEvent("notification_sent", Bundle().apply {
			putDouble("chance", chance.value ?: -1.0)
		})
	}

	override fun logManualRefresh() {
		firebaseAnalytics.logEvent("manual_refresh", Bundle())
	}

	override fun setNotificationsEnabled(enabled: Boolean) {
		firebaseAnalytics.setUserProperty("notifications_enabled", enabled.toString())
	}

	override fun setNotifyTriggerLevel(level: ChanceLevel) {
		firebaseAnalytics.setUserProperty("notifications_trigger_level", level.name)
	}
}
