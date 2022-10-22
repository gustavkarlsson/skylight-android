package se.gustavkarlsson.skylight.android.feature.background.notifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import se.gustavkarlsson.skylight.android.feature.background.AuroraAlertsChannelId
import se.gustavkarlsson.skylight.android.feature.background.AuroraAlertsChannelName
import javax.inject.Inject

internal class NotificationChannelCreator @Inject constructor(
    private val notificationManager: NotificationManager,
    @AuroraAlertsChannelId private val id: String,
    @AuroraAlertsChannelName private val name: String,
) {

    fun createChannel() {
        val channel = NotificationChannel(id, name, NotificationManager.IMPORTANCE_HIGH).apply {
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }
        notificationManager.createNotificationChannel(channel)
    }
}
