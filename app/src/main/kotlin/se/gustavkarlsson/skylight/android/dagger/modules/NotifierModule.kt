package se.gustavkarlsson.skylight.android.dagger.modules

import android.app.NotificationManager
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.Reusable
import se.gustavkarlsson.skylight.android.dagger.LAST_NOTIFIED_NAME
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services.Notifier
import se.gustavkarlsson.skylight.android.services.SingletonCache
import se.gustavkarlsson.skylight.android.services_impl.cache.DualSingletonCache
import se.gustavkarlsson.skylight.android.services_impl.cache.auroraReportCacheSerializer
import se.gustavkarlsson.skylight.android.services_impl.notifications.AuroraReportNotifier
import javax.inject.Named
import javax.inject.Singleton

@Module
class NotifierModule {

	@Provides
	@Singleton
	@Named(LAST_NOTIFIED_NAME)
	fun provideLastNotifiedAuroraReportCache(
		context: Context
	): SingletonCache<AuroraReport> = DualSingletonCache(LAST_NOTIFIED_CACHE_ID, AuroraReport.empty, auroraReportCacheSerializer, context)

    @Provides
    @Reusable
    fun provideNotifier(
		context: Context,
		notificationManager: NotificationManager,
		chanceEvaluator: ChanceEvaluator<AuroraReport>
	): Notifier<AuroraReport> = AuroraReportNotifier(context, notificationManager, chanceEvaluator)

	companion object {
		private const val LAST_NOTIFIED_CACHE_ID = "last-notified-aurora-report"
	}
}
