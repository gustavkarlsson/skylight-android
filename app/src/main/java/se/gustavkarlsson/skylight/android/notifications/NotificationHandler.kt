package se.gustavkarlsson.skylight.android.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.support.v4.app.TaskStackBuilder
import dagger.Reusable
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.cache.SingletonCache
import se.gustavkarlsson.skylight.android.dagger.Names.LAST_NOTIFIED_NAME
import se.gustavkarlsson.skylight.android.evaluation.ChanceEvaluator
import se.gustavkarlsson.skylight.android.evaluation.ChanceLevel
import se.gustavkarlsson.skylight.android.gui.activities.main.MainActivity
import se.gustavkarlsson.skylight.android.models.AuroraReport
import javax.inject.Inject
import javax.inject.Named

@Reusable
class NotificationHandler @Inject
internal constructor(
		private val context: Context,
		private val notificationManager: NotificationManager,
		@param:Named(LAST_NOTIFIED_NAME) private val lastNotifiedReportCache: SingletonCache<AuroraReport>,
		private val evaluator: ChanceEvaluator<AuroraReport>,
		private val decider: NotificationDecider
) {

    fun handle(report: AuroraReport) {
        if (decider.shouldNotify(report)) {
            notify(report)
            lastNotifiedReportCache.set(report)
        }
    }

    private fun notify(report: AuroraReport) {
        val chance = evaluator.evaluate(report)
        val chanceLevel = ChanceLevel.fromChance(chance)
        val text = context.getString(chanceLevel.resourceId)
        val pendingIntent = createActivityPendingIntent()

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

        notificationManager.notify(135551, notification)
    }

    private fun createActivityPendingIntent(): PendingIntent {
        val mainActivityIntent = Intent(context, MainActivity::class.java)
        val stackBuilder = TaskStackBuilder.create(context)
        stackBuilder.addParentStack(MainActivity::class.java)
        stackBuilder.addNextIntent(mainActivityIntent)
        return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}
