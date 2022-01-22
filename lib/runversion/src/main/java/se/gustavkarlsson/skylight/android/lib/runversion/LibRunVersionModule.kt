package se.gustavkarlsson.skylight.android.lib.runversion

import android.content.Context
import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.core.VersionCode

@Module
object LibRunVersionModule {

    @Provides
    internal fun runVersionManager(
        context: Context,
        @VersionCode versionCode: Int,
    ): RunVersionManager =
        SharedPreferencesRunVersionManager(
            context,
            versionCode,
        )
}
