package se.gustavkarlsson.skylight.android.dagger.modules

import dagger.Module
import dagger.Provides
import dagger.Reusable
import org.threeten.bp.Clock

@Module
class ClockModule {

    @Provides
    @Reusable
    fun provideClock(): Clock = Clock.systemUTC()

}
