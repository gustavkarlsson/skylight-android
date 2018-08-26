package se.gustavkarlsson.skylight.android.background.di.modules

import android.app.Activity
import android.app.KeyguardManager
import android.app.NotificationManager
import android.content.Context
import se.gustavkarlsson.skylight.android.background.notifications.*
import se.gustavkarlsson.skylight.android.di.modules.*
import se.gustavkarlsson.skylight.android.entities.AuroraReport

class AndroidNotificationModule(
	contextModule: ContextModule,
	formattingModule: FormattingModule,
	evaluationModule: EvaluationModule,
	krateModule: KrateModule,
	timeModule: TimeModule,
	notifiedChanceRepositoryModule: NotifiedChanceRepositoryModule,
	activityClass: Class<out Activity>
) : NotificationModule {

	override val notifier: Notifier<AuroraReport>  by lazy {
		val notificationManager = contextModule.context
			.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		AuroraReportNotifier(
			contextModule.context,
			notificationManager,
			formattingModule.chanceLevelFormatter,
			evaluationModule.auroraReportEvaluator,
			activityClass
		)
	}

	override val decider: AuroraReportNotificationDecider by lazy {
		AuroraReportNotificationDeciderImpl(
			notifiedChanceRepositoryModule.notifiedChanceRepository,
			evaluationModule.auroraReportEvaluator,
			krateModule.store,
			OutdatedEvaluator(timeModule.timeProvider),
			AppVisibilityEvaluator(
				contextModule.context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
			)
		)
	}
}
