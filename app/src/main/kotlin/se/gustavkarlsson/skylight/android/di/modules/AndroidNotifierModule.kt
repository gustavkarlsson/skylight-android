package se.gustavkarlsson.skylight.android.di.modules

import android.app.NotificationManager
import android.content.Context
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.persistence.LastNotifiedChanceRoomRepository
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.LastNotifiedChanceRepository
import se.gustavkarlsson.skylight.android.services.Notifier
import se.gustavkarlsson.skylight.android.services.formatters.SingleValueFormatter
import se.gustavkarlsson.skylight.android.services_impl.notifications.AuroraReportNotifier

class AndroidNotifierModule(
	context: Context,
	chanceLevelFormatter: SingleValueFormatter<ChanceLevel>,
	chanceEvaluator: ChanceEvaluator<AuroraReport>
) : NotifierModule {

	override val lastNotifiedChanceRepository: LastNotifiedChanceRepository by lazy {
		LastNotifiedChanceRoomRepository(context)
	}

	override val notifier: Notifier<AuroraReport>  by lazy {
		val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		AuroraReportNotifier(context, notificationManager, chanceLevelFormatter, chanceEvaluator)
	}
}
