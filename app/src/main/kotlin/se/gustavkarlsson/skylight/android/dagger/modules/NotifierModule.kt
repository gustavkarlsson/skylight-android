package se.gustavkarlsson.skylight.android.dagger.modules

import android.app.NotificationManager
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.Reusable
import se.gustavkarlsson.skylight.android.caching.AuroraReportDualCache
import se.gustavkarlsson.skylight.android.services.SingletonCache
import se.gustavkarlsson.skylight.android.dagger.qualifiers.LastNotified
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.entities.ChanceLevel
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.Notifier
import se.gustavkarlsson.skylight.android.services.formatters.SingleValueFormatter
import se.gustavkarlsson.skylight.android.services_impl.notifications.AuroraReportNotifier
import javax.inject.Singleton

@Module
class NotifierModule {

	@Provides
	@Singleton
	@LastNotified
	fun provideLastNotifiedAuroraReportCache(
		context: Context
	): SingletonCache<AuroraReport> =
		AuroraReportDualCache(context, LAST_NOTIFIED_CACHE_ID, AuroraReport.empty)

	@Provides
	@Reusable
	fun provideNotifier(
		context: Context,
		notificationManager: NotificationManager,
		chanceLevelFormatter: SingleValueFormatter<ChanceLevel>,
		chanceEvaluator: ChanceEvaluator<AuroraReport>
	): Notifier<AuroraReport> =
		AuroraReportNotifier(context, notificationManager, chanceLevelFormatter, chanceEvaluator)

	companion object {
		private const val LAST_NOTIFIED_CACHE_ID = "last-notified-aurora-report"
	}
}
