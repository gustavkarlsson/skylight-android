package se.gustavkarlsson.skylight.android.lib.runversion

import android.content.Context
import dagger.Module
import dagger.Provides
import javax.inject.Named
import se.gustavkarlsson.skylight.android.core.AppScope

@Module
object LibRunVersionModule {

    @Provides
    @AppScope
    internal fun runVersionManager(
        context: Context,
        @Named("versionCode") versionCode: Int
    ): RunVersionManager =
        SharedPreferencesRunVersionManager(
            context,
            versionCode
        )
}
