package se.gustavkarlsson.skylight.android.dagger.modules

import dagger.Module
import dagger.Provides
import dagger.Reusable
import io.reactivex.Single
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import se.gustavkarlsson.skylight.android.services.providers.TimeProvider
import se.gustavkarlsson.skylight.android.services_impl.providers.SystemTimeWithFixedZoneIdProvider
import javax.inject.Singleton

@Module
class TimeModule {

    @Provides
    @Singleton
    fun provideTimeProvider(): TimeProvider = SystemTimeWithFixedZoneIdProvider(ZoneId.systemDefault())

	@Provides
	@Reusable
	fun provideNow(timeProvider: TimeProvider): Single<Instant> = timeProvider.getTime()

}
