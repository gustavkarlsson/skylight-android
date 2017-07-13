package se.gustavkarlsson.skylight.android.dagger.modules.replaceable

import dagger.Module
import dagger.Provides
import dagger.Reusable
import se.gustavkarlsson.skylight.android.actions.ShowLastAuroraReport
import se.gustavkarlsson.skylight.android.actions.impl.ShowLastAuroraReportOnPublisher
import se.gustavkarlsson.skylight.android.dagger.LATEST_NAME
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.LastAuroraReportProvider
import se.gustavkarlsson.skylight.android.services.SingletonCache
import se.gustavkarlsson.skylight.android.services.StreamPublisher
import se.gustavkarlsson.skylight.android.util.UserFriendlyException
import javax.inject.Named

@Module
class ShowLastAuroraReportModule {

	@Provides
	@Reusable
	fun provideShowLastAuroraReport(lastAuroraReportProvider: LastAuroraReportProvider, @Named(LATEST_NAME) auroraReports: StreamPublisher<AuroraReport>, errors: StreamPublisher<UserFriendlyException>): ShowLastAuroraReport {
		return ShowLastAuroraReportOnPublisher(lastAuroraReportProvider, auroraReports, errors)
	}

	@Provides
	@Reusable
	fun provideLastAuroraReportProvider(@Named(LATEST_NAME) cache: SingletonCache<AuroraReport>): LastAuroraReportProvider {
		// TODO Replace with real implementation
		return object : LastAuroraReportProvider {
			override fun get(): AuroraReport {
				return cache.value
			}
		}
	}
}
