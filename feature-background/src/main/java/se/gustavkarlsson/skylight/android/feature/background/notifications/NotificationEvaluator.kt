package se.gustavkarlsson.skylight.android.feature.background.notifications

internal interface NotificationEvaluator {
	fun shouldNotify(notification: Notification): Boolean
	fun onNotified(notification: Notification)
}
