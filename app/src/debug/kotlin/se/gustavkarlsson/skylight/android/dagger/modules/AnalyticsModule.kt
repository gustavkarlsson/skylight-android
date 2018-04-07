package se.gustavkarlsson.skylight.android.dagger.modules

import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.services.Analytics
import se.gustavkarlsson.skylight.android.services_impl.NullAnalytics
import javax.inject.Singleton

@Module
class AnalyticsModule {

	@Provides
	@Singleton
	fun provideAnalytics(): Analytics = NullAnalytics()

}
