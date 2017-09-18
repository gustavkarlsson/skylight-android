package se.gustavkarlsson.skylight.android.dagger.modules

import dagger.Module
import dagger.Provides
import dagger.Reusable
import org.threeten.bp.Clock
import org.threeten.bp.ZoneId
import org.threeten.bp.ZoneOffset
import se.gustavkarlsson.skylight.android.util.ZoneIdProvider

@Module
class TimeModule {

    @Provides
    @Reusable
    fun provideClock(): Clock = Clock.systemUTC()

	@Provides
	@Reusable
	fun provideZoneIdProvider(): ZoneIdProvider {
        return object : ZoneIdProvider {
            override val zoneId: ZoneId
                get() = ZoneOffset.systemDefault()
        }
    }

}
