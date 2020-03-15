package se.gustavkarlsson.skylight.android.lib.time

import dagger.Module
import dagger.Provides
import dagger.Reusable
import se.gustavkarlsson.skylight.android.services.Time

@Module
class LibTimeModule {

    @Provides
    @Reusable
    internal fun time(): Time = SystemTime
}
