package se.gustavkarlsson.skylight.android.lib.time

import dagger.Module
import dagger.Provides

@Module
object LibTimeModule {

    @Provides
    internal fun time(): Time = SystemTime
}
