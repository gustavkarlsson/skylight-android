package se.gustavkarlsson.skylight.android.dagger.modules.replaceable

import dagger.Module
import dagger.Provides
import dagger.Reusable
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.dagger.FOREGROUND_UPDATE_TIMEOUT_NAME
import javax.inject.Named

@Module
class ForegroundUpdateTimeoutModule {

    // Published
    @Provides
    @Reusable
    @Named(FOREGROUND_UPDATE_TIMEOUT_NAME)
    fun provideForegroundUpdateTimeout(): Duration {
        return Duration.ofSeconds(10)
    }

}
