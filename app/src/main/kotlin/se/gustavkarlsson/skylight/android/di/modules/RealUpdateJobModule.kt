package se.gustavkarlsson.skylight.android.di.modules

import android.app.KeyguardManager
import android.content.Context
import se.gustavkarlsson.skylight.android.persistence.LastNotifiedChanceRoomRepository
import se.gustavkarlsson.skylight.android.services_impl.AppVisibilityEvaluator
import se.gustavkarlsson.skylight.android.services_impl.notifications.AuroraReportNotificationDecider
import se.gustavkarlsson.skylight.android.services_impl.notifications.OutdatedEvaluator
import se.gustavkarlsson.skylight.android.services_impl.scheduling.UpdateJob

class RealUpdateJobModule(
	contextModule: ContextModule,
	timeModule: TimeModule,
	evaluationModule: EvaluationModule,
	settingsModule: SettingsModule,
	auroraReportModule: AuroraReportModule,
	notifierModule: NotifierModule
) : UpdateJobModule {

	override val updateJob: UpdateJob by lazy {
		val lastNotifiedChanceRepository = LastNotifiedChanceRoomRepository(contextModule.context)
		val outdatedEvaluator = OutdatedEvaluator(timeModule.timeProvider)
		val keyguardManager =
			contextModule.context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
		val appVisibilityEvaluator = AppVisibilityEvaluator(keyguardManager)
		val auroraReportNotificationDecider = AuroraReportNotificationDecider(
			lastNotifiedChanceRepository,
			evaluationModule.auroraReportEvaluator,
			settingsModule.settings,
			outdatedEvaluator,
			appVisibilityEvaluator
		)
		UpdateJob(
			auroraReportModule.auroraReportSingle,
			auroraReportNotificationDecider,
			notifierModule.notifier
		)
	}
}
