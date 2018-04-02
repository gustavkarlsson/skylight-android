package se.gustavkarlsson.skylight.android.dagger.modules

import android.app.NotificationManager
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.Reusable
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.persistence.LastNotifiedChanceRoomRepository
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.LastNotifiedChanceRepository
import se.gustavkarlsson.skylight.android.services.Notifier
import se.gustavkarlsson.skylight.android.services.formatters.SingleValueFormatter
import se.gustavkarlsson.skylight.android.services_impl.notifications.AuroraReportNotifier
import javax.inject.Singleton

@Module
class NotifierModule {

	@Provides
	@Singleton
	fun provideLastNotifiedChanceRepository(
		context: Context
	): LastNotifiedChanceRepository = LastNotifiedChanceRoomRepository(context)

	@Provides
	@Reusable
	fun provideNotifier(
		context: Context,
		notificationManager: NotificationManager,
		chanceLevelFormatter: SingleValueFormatter<ChanceLevel>,
		chanceEvaluator: ChanceEvaluator<AuroraReport>
	): Notifier<AuroraReport> =
		AuroraReportNotifier(context, notificationManager, chanceLevelFormatter, chanceEvaluator)
}
