package se.gustavkarlsson.skylight.android.background.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

internal class NotificationChannelCreator(
	private val notificationManager: NotificationManager,
	private val id: String,
	private val name: String
) {

	fun createChannel() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val channel = NotificationChannel(
				id,
				name,
				NotificationManager.IMPORTANCE_HIGH
			).apply {
				lockscreenVisibility = Notification.VISIBILITY_PUBLIC
			}
			notificationManager.createNotificationChannel(channel)
		}
	}
}
