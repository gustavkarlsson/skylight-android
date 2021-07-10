package se.gustavkarlsson.skylight.android.feature.background.notifications

import android.app.Activity
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.app.NotificationCompat
import se.gustavkarlsson.skylight.android.core.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.core.services.Formatter
import se.gustavkarlsson.skylight.android.feature.background.R
import se.gustavkarlsson.skylight.android.lib.analytics.Analytics
import se.gustavkarlsson.skylight.android.lib.places.PlaceId
import se.gustavkarlsson.skylight.android.lib.places.setPlaceId

internal class NotifierImpl(
    private val context: Context,
    private val notificationManager: NotificationManager,
    private val notificationFormatter: Formatter<Notification>,
    private val activityClass: Class<out Activity>,
    private val channelId: String,
    private val analytics: Analytics
) : Notifier {

    override fun notify(notification: Notification) {
        val androidNotification = NotificationCompat.Builder(context, channelId).run {
            setSmallIcon(R.drawable.app_logo_small)
            setContentTitle(context.getString(R.string.possible_aurora))
            color = createColor()
            setText(notification)
            setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
            setAutoCancel(true)
            priority = createPriority(notification)
            setDefaults(NotificationCompat.DEFAULT_ALL)
            val placeId = notification.data.map { it.place.id }.first()
            setContentIntent(createActivityPendingIntent(placeId))
            build()
        }

        notificationManager.notify(1, androidNotification)
        analytics.logEvent("notification_sent")
    }

    private fun NotificationCompat.Builder.setText(notification: Notification) {
        val lines = createText(notification).lines()
        setContentText(lines.first())
        val inboxStyle = NotificationCompat.InboxStyle()
        lines.forEach {
            inboxStyle.addLine(it)
        }
        setStyle(inboxStyle)
    }

    private fun createPriority(notification: Notification): Int =
        when (notification.data.map { it.chanceLevel }.maxOrNull()) {
            ChanceLevel.HIGH -> NotificationCompat.PRIORITY_HIGH
            ChanceLevel.MEDIUM -> NotificationCompat.PRIORITY_DEFAULT
            ChanceLevel.LOW -> NotificationCompat.PRIORITY_LOW
            ChanceLevel.NONE, ChanceLevel.UNKNOWN, null -> NotificationCompat.PRIORITY_MIN
        }

    private fun createColor(): Int = context.theme.resolveColor(R.attr.colorPrimary)

    private fun createText(notification: Notification) =
        notificationFormatter.format(notification).resolve(context)

    // FIXME test that it selects the correct place
    private fun createActivityPendingIntent(placeId: PlaceId): PendingIntent {
        val intent = Intent(context, activityClass).apply {
            val bundle = Bundle().apply { setPlaceId(placeId) }
            putExtras(bundle)
        }
        return PendingIntent.getActivity(
            context,
            -1,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
    }
}

@ColorInt
private fun Resources.Theme.resolveColor(@AttrRes attr: Int): Int {
    val typedValue = TypedValue()
    resolveAttribute(attr, typedValue, true)
    return typedValue.data
}
