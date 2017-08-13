package se.gustavkarlsson.skylight.android.dagger.modules.replaceable

import dagger.Module
import dagger.Provides
import dagger.Reusable
import se.gustavkarlsson.skylight.android.actions.SetUpdateSchedule
import se.gustavkarlsson.skylight.android.actions_impl.SetUpdateScheduleBasedOnSettings
import se.gustavkarlsson.skylight.android.services.Scheduler
import se.gustavkarlsson.skylight.android.services.Settings

@Module
class SetUpdateScheduleModule {

	@Provides
	@Reusable
	fun provideSetUpdateSchedule(settings: Settings, scheduler: Scheduler): SetUpdateSchedule {
		return SetUpdateScheduleBasedOnSettings(settings, scheduler)
	}
}
