package se.gustavkarlsson.skylight.android.feature.background.notifications

internal interface NotificationEvaluator {
    suspend fun shouldNotify(notification: Notification): Boolean
    suspend fun onNotified(notification: Notification)
}
