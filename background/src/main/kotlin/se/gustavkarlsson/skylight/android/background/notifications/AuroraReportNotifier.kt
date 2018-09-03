package se.gustavkarlsson.skylight.android.background.notifications

import android.app.Activity
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.TaskStackBuilder
import se.gustavkarlsson.skylight.android.background.R
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.services.Analytics
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.formatters.SingleValueFormatter

internal class AuroraReportNotifier(
	private val context: Context,
	private val notificationManager: NotificationManager,
	private val chanceLevelFormatter: SingleValueFormatter<ChanceLevel>,
	private val chanceEvaluator: ChanceEvaluator<AuroraReport>,
	private val activityClass: Class<out Activity>
) : Notifier<AuroraReport> {

    override fun notify(value: AuroraReport) {
        val chance = chanceEvaluator.evaluate(value)
        val chanceLevel = ChanceLevel.fromChance(chance)
        val text = chanceLevelFormatter.format(chanceLevel)
        val pendingIntent = createActivityPendingIntent()

		// TODO Implement notification channels: https://developer.android.com/guide/topics/ui/notifiers/notifications.html#ManageChannels
        val notification = NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.app_logo_small)
                .setContentTitle(context.getString(R.string.possible_aurora))
                .setContentText(text)
                .setCategory(NotificationCompat.CATEGORY_RECOMMENDATION)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .build()

        notificationManager.notify(1, notification)
		Analytics.logNotificationSent(chance)
    }

    private fun createActivityPendingIntent(): PendingIntent {
        val mainActivityIntent = Intent(context, activityClass)
        val stackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addParentStack(activityClass)
        stackBuilder.addNextIntent(mainActivityIntent)
        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)!!
    }
}
