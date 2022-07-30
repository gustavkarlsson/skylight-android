package se.gustavkarlsson.skylight.android.lib.time

import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.core.AppScopeMarker

@Module
@ContributesTo(AppScopeMarker::class)
object LibTimeModule {

    @Provides
    internal fun time(): Time = SystemTime
}
