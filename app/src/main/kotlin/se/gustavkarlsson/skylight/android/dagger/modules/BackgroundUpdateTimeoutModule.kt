package se.gustavkarlsson.skylight.android.dagger.modules

import dagger.Module
import dagger.Provides
import dagger.Reusable
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.dagger.BACKGROUND_UPDATE_TIMEOUT_NAME
import javax.inject.Named

@Module
class BackgroundUpdateTimeoutModule {

    @Provides
    @Reusable
    @Named(BACKGROUND_UPDATE_TIMEOUT_NAME)
    fun provideBackgroundUpdateTimeout(): Duration = Duration.ofSeconds(30)

}
