package se.gustavkarlsson.skylight.android.services_impl.presenters

import dagger.Reusable
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.Notifier
import se.gustavkarlsson.skylight.android.services.Presenter
import se.gustavkarlsson.skylight.android.services_impl.notifications.NotificationEvaluator
import javax.inject.Inject

@Reusable
class AuroraReportNotificationPresenter
@Inject
constructor(
        private val evaluator: NotificationEvaluator,
        private val notifier: Notifier<AuroraReport>
) : Presenter<AuroraReport> { // TODO Use this in UpdateJob

    override fun present(value: AuroraReport) {
        if (evaluator.shouldNotify(value)) {
            notifier.notify(value)
            evaluator.onNotified(value)
        }
    }
}
