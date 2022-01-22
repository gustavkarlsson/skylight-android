package se.gustavkarlsson.skylight.android.lib.runversion

import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.core.AppScopeMarker

@Module
@ContributesTo(AppScopeMarker::class)
object LibRunVersionModule {

    @Provides
    internal fun runVersionManager(impl: SharedPreferencesRunVersionManager): RunVersionManager = impl
}
