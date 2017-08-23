package se.gustavkarlsson.skylight.android.dagger.modules.replaceable

import android.app.NotificationManager
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.Reusable
import se.gustavkarlsson.skylight.android.dagger.LAST_NOTIFIED_NAME
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services_impl.notifications.NotificationTracker
import se.gustavkarlsson.skylight.android.services_impl.notifications.ReportOutdatedEvaluator
import se.gustavkarlsson.skylight.android.services.Notifier
import se.gustavkarlsson.skylight.android.services.Presenter
import se.gustavkarlsson.skylight.android.services.Settings
import se.gustavkarlsson.skylight.android.services.SingletonCache
import se.gustavkarlsson.skylight.android.services.evaluation.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services_impl.notifications.AuroraReportNotifier
import se.gustavkarlsson.skylight.android.services_impl.presenters.AuroraReportNotificationPresenter
import javax.inject.Named

@Module
class AuroraReportNotificationPresenterModule {

    // Published
	@Provides
	@Reusable
	fun provideAuroraReportsPresenter(tracker: NotificationTracker, notifier: Notifier<AuroraReport>): Presenter<AuroraReport> {
		return AuroraReportNotificationPresenter(tracker, notifier)
	}

    @Provides
    @Reusable
    fun provideNotificationTracker(@Named(LAST_NOTIFIED_NAME) lastNotifiedReportCache: SingletonCache<AuroraReport>, evaluator: ChanceEvaluator<AuroraReport>, settings: Settings, outdatedEvaluator: ReportOutdatedEvaluator): NotificationTracker {
        return NotificationTracker(lastNotifiedReportCache, evaluator, settings, outdatedEvaluator)
    }

    @Provides
    @Reusable
    fun provideAuroraReportNotifier(context: Context, notificationManager: NotificationManager, evaluator: ChanceEvaluator<AuroraReport>): Notifier<AuroraReport> {
        return AuroraReportNotifier(context, notificationManager, evaluator)
    }
}
