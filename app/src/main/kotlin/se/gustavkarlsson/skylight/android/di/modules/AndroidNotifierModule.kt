package se.gustavkarlsson.skylight.android.di.modules

import android.app.NotificationManager
import android.content.Context
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.persistence.LastNotifiedChanceRoomRepository
import se.gustavkarlsson.skylight.android.services.LastNotifiedChanceRepository
import se.gustavkarlsson.skylight.android.services.Notifier
import se.gustavkarlsson.skylight.android.services_impl.notifications.AuroraReportNotifier

class AndroidNotifierModule(
	contextModule: ContextModule,
	formattingModule: FormattingModule,
	evaluationModule: EvaluationModule
) : NotifierModule {

	override val lastNotifiedChanceRepository: LastNotifiedChanceRepository by lazy {
		LastNotifiedChanceRoomRepository(contextModule.context)
	}

	override val notifier: Notifier<AuroraReport>  by lazy {
		val notificationManager = contextModule.context
			.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		AuroraReportNotifier(
			contextModule.context,
			notificationManager,
			formattingModule.chanceLevelFormatter,
			evaluationModule.auroraReportEvaluator
		)
	}
}
