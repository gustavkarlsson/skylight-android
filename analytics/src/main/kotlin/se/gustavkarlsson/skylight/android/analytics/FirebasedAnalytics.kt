package se.gustavkarlsson.skylight.android.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import se.gustavkarlsson.skylight.android.entities.Chance
import se.gustavkarlsson.skylight.android.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.services.Analytics

internal class FirebasedAnalytics(private val firebaseAnalytics: FirebaseAnalytics) : Analytics {

	override fun logNotificationSent(chance: Chance) {
		firebaseAnalytics.logEvent("notification_sent", Bundle().apply {
			putDouble("chance", chance.value ?: -1.0)
		})
	}

	override fun setNotificationsEnabled(enabled: Boolean) {
		firebaseAnalytics.setUserProperty("notifications_enabled", enabled.toString())
	}

	override fun setNotifyTriggerLevel(triggerLevel: ChanceLevel) {
		firebaseAnalytics.setUserProperty("notifications_level", triggerLevel.name)
	}
}
