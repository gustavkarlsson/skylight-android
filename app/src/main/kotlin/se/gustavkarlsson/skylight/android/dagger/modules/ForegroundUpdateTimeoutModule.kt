package se.gustavkarlsson.skylight.android.dagger.modules

import dagger.Module
import dagger.Provides
import dagger.Reusable
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.dagger.FOREGROUND_UPDATE_TIMEOUT_NAME
import javax.inject.Named

@Module
class ForegroundUpdateTimeoutModule {

    @Provides
    @Reusable
    @Named(FOREGROUND_UPDATE_TIMEOUT_NAME)
    fun provideForegroundUpdateTimeout(): Duration = Duration.ofSeconds(10)

}
