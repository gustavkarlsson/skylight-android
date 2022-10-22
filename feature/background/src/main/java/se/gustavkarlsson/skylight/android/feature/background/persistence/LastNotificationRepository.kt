package se.gustavkarlsson.skylight.android.feature.background.persistence

import se.gustavkarlsson.skylight.android.feature.background.notifications.Notification

internal interface LastNotificationRepository {
    suspend fun get(): NotificationRecord?
    suspend fun insert(notification: Notification)
}
