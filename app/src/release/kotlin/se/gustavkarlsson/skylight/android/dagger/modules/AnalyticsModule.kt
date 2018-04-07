package se.gustavkarlsson.skylight.android.dagger.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import se.gustavkarlsson.skylight.android.services.Analytics
import se.gustavkarlsson.skylight.android.services_impl.FirebasedAnalytics
import javax.inject.Singleton

@Module
class AnalyticsModule {

	@Provides
	@Singleton
	fun provideAnalytics(context: Context): Analytics = FirebasedAnalytics(context)
}
