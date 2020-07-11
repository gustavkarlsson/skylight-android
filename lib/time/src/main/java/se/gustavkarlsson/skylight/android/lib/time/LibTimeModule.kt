package se.gustavkarlsson.skylight.android.lib.time

import dagger.Module
import dagger.Provides
import dagger.Reusable

@Module
object LibTimeModule {

    @Provides
    @Reusable
    internal fun time(): Time = SystemTime
}
