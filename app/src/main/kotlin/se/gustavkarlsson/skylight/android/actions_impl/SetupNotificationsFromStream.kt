package se.gustavkarlsson.skylight.android.actions_impl

import dagger.Reusable
import se.gustavkarlsson.skylight.android.actions.SetupNotifications
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.Notifier
import se.gustavkarlsson.skylight.android.services.streams.Stream
import se.gustavkarlsson.skylight.android.services_impl.notifications.AuroraReportNotificationEvaluator
import javax.inject.Inject

@Reusable
class SetupNotificationsFromStream
@Inject
constructor(
        private val stream: Stream<AuroraReport>,
        private val evaluator: AuroraReportNotificationEvaluator,
        private val notifier: Notifier<AuroraReport>
) : SetupNotifications {
    private var hasRun = false

    override fun invoke() {
        check(!hasRun) { "Already ran" }
        stream.subscribe {
            if (evaluator.shouldNotify(it)) {
                notifier.notify(it)
                evaluator.onNotified(it)
            }
		}
        hasRun = true
    }
}
