package se.gustavkarlsson.skylight.android.services_impl.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.app.TaskStackBuilder
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.gui.activities.main.MainActivity
import se.gustavkarlsson.skylight.android.services.Notifier
import se.gustavkarlsson.skylight.android.services.evaluation.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.evaluation.ChanceLevel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuroraReportNotifier
@Inject
constructor(
        private val context: Context,
        private val notificationManager: NotificationManager,
        private val chanceEvaluator: ChanceEvaluator<AuroraReport>
) : Notifier<AuroraReport> {

    override fun notify(value: AuroraReport) {
        val chance = chanceEvaluator.evaluate(value)
        val chanceLevel = ChanceLevel.fromChance(chance)
        val text = context.getString(chanceLevel.resourceId)
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
    }

    private fun createActivityPendingIntent(): PendingIntent {
        val mainActivityIntent = Intent(context, MainActivity::class.java)
        val stackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addParentStack(MainActivity::class.java)
        stackBuilder.addNextIntent(mainActivityIntent)
        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}
