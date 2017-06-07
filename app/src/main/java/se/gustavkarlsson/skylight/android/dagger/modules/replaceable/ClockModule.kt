package se.gustavkarlsson.skylight.android.dagger.modules.replaceable

import dagger.Module
import dagger.Provides
import dagger.Reusable
import org.threeten.bp.Clock

@Module
class ClockModule {

    // Published
    @Provides
    @Reusable
    internal fun provideClock(): Clock {
        return Clock.systemUTC()
    }

}
