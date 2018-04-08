package se.gustavkarlsson.skylight.android.di.modules

import android.app.KeyguardManager
import android.content.Context
import io.reactivex.Single
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.persistence.LastNotifiedChanceRoomRepository
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.Notifier
import se.gustavkarlsson.skylight.android.services.Settings
import se.gustavkarlsson.skylight.android.services.providers.TimeProvider
import se.gustavkarlsson.skylight.android.services_impl.AppVisibilityEvaluator
import se.gustavkarlsson.skylight.android.services_impl.notifications.AuroraReportNotificationDecider
import se.gustavkarlsson.skylight.android.services_impl.notifications.OutdatedEvaluator
import se.gustavkarlsson.skylight.android.services_impl.scheduling.UpdateJob

class RealUpdateJobModule(
	context: Context,
	timeProvider: TimeProvider,
	auroraReportChanceEvaluator: ChanceEvaluator<AuroraReport>,
	settings: Settings,
	auroraReportSingle: Single<AuroraReport>,
	notifier: Notifier<AuroraReport>
) : UpdateJobModule {

	override val updateJob: UpdateJob by lazy {
		val lastNotifiedChanceRepository = LastNotifiedChanceRoomRepository(context)
		val outdatedEvaluator = OutdatedEvaluator(timeProvider)
		val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
		val appVisibilityEvaluator = AppVisibilityEvaluator(keyguardManager)
		val auroraReportNotificationDecider = AuroraReportNotificationDecider(
			lastNotifiedChanceRepository,
			auroraReportChanceEvaluator,
			settings,
			outdatedEvaluator,
			appVisibilityEvaluator
		)
		UpdateJob(auroraReportSingle, auroraReportNotificationDecider, notifier)
	}
}
