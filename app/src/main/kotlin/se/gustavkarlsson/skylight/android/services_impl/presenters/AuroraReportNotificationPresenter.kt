package se.gustavkarlsson.skylight.android.services_impl.presenters

import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services_impl.notifications.NotificationTracker
import se.gustavkarlsson.skylight.android.services.Notifier
import se.gustavkarlsson.skylight.android.services.Presenter

class AuroraReportNotificationPresenter(
        private val tracker: NotificationTracker,
        private val notifier: Notifier<AuroraReport>
) : Presenter<AuroraReport> {

    override fun present(value: AuroraReport) {
        if (tracker.shouldNotify(value)) {
            notifier.notify(value)
			tracker.onNotified(value)
        }
    }
}
