package se.gustavkarlsson.skylight.android.actions_impl

import se.gustavkarlsson.skylight.android.actions.SetUpdateSchedule
import se.gustavkarlsson.skylight.android.services.Scheduler
import se.gustavkarlsson.skylight.android.services.Settings

class SetUpdateScheduleBasedOnSettings(
	private val settings: Settings,
	private val scheduler : Scheduler
) : SetUpdateSchedule {
	override fun invoke() {
		if (settings.isEnableNotifications) {
			scheduler.schedule()
		} else {
			scheduler.unschedule()
		}
	}
}
