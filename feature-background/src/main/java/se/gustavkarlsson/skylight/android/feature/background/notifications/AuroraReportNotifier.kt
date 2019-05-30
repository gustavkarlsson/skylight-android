package se.gustavkarlsson.skylight.android.feature.background.notifications

import android.app.Activity
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import se.gustavkarlsson.skylight.android.feature.background.R
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.lib.analytics.Analytics
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.Formatter

internal class AuroraReportNotifier(
	private val context: Context,
	private val notificationManager: NotificationManager,
	private val chanceLevelFormatter: Formatter<ChanceLevel>,
	private val chanceEvaluator: ChanceEvaluator<AuroraReport>,
	private val activityClass: Class<out Activity>,
	private val channelId: String,
	private val analytics: Analytics
) : Notifier<AuroraReport> {

	override fun notify(value: AuroraReport) {
		val chance = chanceEvaluator.evaluate(value)
		val chanceLevel = ChanceLevel.fromChance(chance)
		val text = chanceLevelFormatter.format(chanceLevel).resolve(context)
		val pendingIntent = createActivityPendingIntent()

		val tintColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			context.resources.getColor(R.color.primary, null)
		} else {
			null
		}

		val notification = NotificationCompat.Builder(context, channelId).run {
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

		notificationManager.notify(1, notification)
		analytics.logNotificationSent(chance)
	}

	private fun createActivityPendingIntent(): PendingIntent {
		val mainActivityIntent = Intent(context, activityClass)
		return PendingIntent.getActivity(context, -1, mainActivityIntent, PendingIntent.FLAG_UPDATE_CURRENT)
	}
}
