package se.gustavkarlsson.skylight.android.dagger.modules.replaceable

import android.app.NotificationManager
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.Reusable
import se.gustavkarlsson.skylight.android.dagger.LAST_NOTIFIED_NAME
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.notifications.NotificationDecider
import se.gustavkarlsson.skylight.android.services.Presenter
import se.gustavkarlsson.skylight.android.services.SingletonCache
import se.gustavkarlsson.skylight.android.services.evaluation.ChanceEvaluator
import se.gustavkarlsson.skylight.android.services_impl.presenters.AuroraReportNotificationPresenter
import javax.inject.Named

@Module
class AuroraReportNotificationPresenterModule {

    // Published
	@Provides
	@Reusable
	fun provideAuroraReportsPresenter(context: Context, notificationManager: NotificationManager, @Named(LAST_NOTIFIED_NAME) lastNotifiedReportCache: SingletonCache<AuroraReport>, evaluator: ChanceEvaluator<AuroraReport>, decider: NotificationDecider): Presenter<AuroraReport> {
		return AuroraReportNotificationPresenter(context, notificationManager, lastNotifiedReportCache, evaluator, decider)
	}
}
