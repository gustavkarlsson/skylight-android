package se.gustavkarlsson.skylight.android

import dagger.Module
import dagger.Provides
import dagger.Reusable
import se.gustavkarlsson.skylight.android.services.Time
import se.gustavkarlsson.skylight.android.time.SystemTime

@Module
class TimeModule {

    @Provides
    @Reusable
    internal fun time(): Time = SystemTime
}
