package se.gustavkarlsson.skylight.android.dagger.modules.replaceable

import dagger.Module
import dagger.Provides
import dagger.Reusable
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.dagger.Names.UPDATE_SCHEDULER_FLEX_NAME
import se.gustavkarlsson.skylight.android.dagger.Names.UPDATE_SCHEDULER_INTERVAL_NAME
import javax.inject.Named

@Module
class UpdateSchedulerModule {

    // Published
    @Provides
    @Reusable
    @Named(UPDATE_SCHEDULER_INTERVAL_NAME)
    fun provideUpdateSchedulerInterval(): Duration {
        return Duration.ofMinutes(20)
    }

    // Published
    @Provides
    @Reusable
    @Named(UPDATE_SCHEDULER_FLEX_NAME)
    fun provideUpdateSchedulerFlex(): Duration {
        return Duration.ofMinutes(10)
    }

}
