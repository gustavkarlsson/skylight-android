package se.gustavkarlsson.skylight.android.dagger.modules

import dagger.Binds
import dagger.Module
import dagger.Reusable
import se.gustavkarlsson.skylight.android.actions.SetUpdateSchedule
import se.gustavkarlsson.skylight.android.actions_impl.SetUpdateScheduleFromSettings

@Module
abstract class SetUpdateScheduleModule {

	@Binds
	@Reusable
	abstract fun bindSetUpdateScheduleBasedOnSettings(impl: SetUpdateScheduleFromSettings): SetUpdateSchedule
}
