package se.gustavkarlsson.skylight.android.feature.background.persistence

import se.gustavkarlsson.skylight.android.feature.background.notifications.Notification

internal interface LastNotificationRepository {
	fun get(): NotificationRecord?
	fun insert(data: Notification)
}
