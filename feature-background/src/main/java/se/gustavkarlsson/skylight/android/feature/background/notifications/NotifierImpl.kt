package se.gustavkarlsson.skylight.android.feature.background.notifications

import android.app.Activity
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import se.gustavkarlsson.skylight.android.feature.background.R
import se.gustavkarlsson.skylight.android.lib.ui.extensions.resolveColor
import se.gustavkarlsson.skylight.android.services.Analytics
import se.gustavkarlsson.skylight.android.services.Formatter

internal class NotifierImpl(
    private val context: Context,
    private val notificationManager: NotificationManager,
    private val notificationFormatter: Formatter<Notification>,
    private val activityClass: Class<out Activity>,
    private val channelId: String,
    private val analytics: Analytics
) : Notifier {

    override fun notify(notification: Notification) {
        val text = notificationFormatter.format(notification).resolve(context)
        val pendingIntent = createActivityPendingIntent()

        val tintColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            context.theme.resolveColor(R.attr.colorPrimary)
        } else {
            null
        }

        val androidNotification = NotificationCompat.Builder(context, channelId).run {
            setSmallIcon(R.drawable.app_logo_small)
            setContentTitle(context.getString(R.string.possible_aurora))
            tintColor?.let { color = it }
            setContentText(text)
            setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
            setAutoCancel(true)
            priority = NotificationCompat.PRIORITY_HIGH
            setDefaults(NotificationCompat.DEFAULT_ALL)
            setContentIntent(pendingIntent)
            build()
        }

        notificationManager.notify(1, androidNotification)
        analytics.logEvent("notification_sent")
    }

    private fun createActivityPendingIntent(): PendingIntent {
        val mainActivityIntent = Intent(context, activityClass)
        return PendingIntent.getActivity(context, -1, mainActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}
