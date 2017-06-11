package se.gustavkarlsson.skylight.android.dagger.modules.replaceable

import dagger.Module
import dagger.Provides
import dagger.Reusable
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.dagger.BACKGROUND_UPDATE_TIMEOUT_NAME
import javax.inject.Named

@Module
class BackgroundUpdateTimeoutModule {

    // Published
    @Provides
    @Reusable
    @Named(BACKGROUND_UPDATE_TIMEOUT_NAME)
    fun provideBackgroundUpdateTimeout(): Duration {
        return Duration.ofSeconds(30)
    }

}
