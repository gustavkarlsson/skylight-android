package se.gustavkarlsson.skylight.android.background.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import se.gustavkarlsson.skylight.android.background.R

internal class NotificationChannelCreator(
	private val context: Context,
	private val notificationManager: NotificationManager,
	private val id: String
) {

	fun createChannel() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val channel = NotificationChannel(
				id,
				context.getString(R.string.aurora_alerts_channel_name),
				NotificationManager.IMPORTANCE_HIGH
			).apply {
				lockscreenVisibility = Notification.VISIBILITY_PUBLIC
			}
			notificationManager.createNotificationChannel(channel)
		}
	}
}
