package se.gustavkarlsson.skylight.android.actions_impl

import dagger.Reusable
import se.gustavkarlsson.skylight.android.actions.SetUpdateSchedule
import se.gustavkarlsson.skylight.android.services.Scheduler
import se.gustavkarlsson.skylight.android.services.Settings
import javax.inject.Inject

@Reusable
class SetUpdateScheduleFromSettings
@Inject
constructor(
        private val settings: Settings,
        private val scheduler: Scheduler
) : SetUpdateSchedule {

    override fun invoke() {
        if (settings.isEnableNotifications) {
            scheduler.schedule()
        } else {
            scheduler.unschedule()
        }
    }
}
