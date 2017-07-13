package se.gustavkarlsson.skylight.android.dagger.modules.replaceable

import dagger.Module
import dagger.Provides
import dagger.Reusable
import se.gustavkarlsson.skylight.android.actions.ShowLastAuroraReport
import se.gustavkarlsson.skylight.android.actions.impl.ShowLastAuroraReportOnPublisher
import se.gustavkarlsson.skylight.android.dagger.LAST_NAME
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.AuroraReportProvider
import se.gustavkarlsson.skylight.android.services.SingletonCache
import se.gustavkarlsson.skylight.android.services.StreamPublisher
import se.gustavkarlsson.skylight.android.util.UserFriendlyException
import javax.inject.Named

@Module
class ShowLastAuroraReportModule {

	@Provides
	@Reusable
	fun provideShowLastAuroraReport(@Named(LAST_NAME) lastAuroraReportProvider: AuroraReportProvider, auroraReports: StreamPublisher<AuroraReport>, errors: StreamPublisher<UserFriendlyException>): ShowLastAuroraReport {
		return ShowLastAuroraReportOnPublisher(lastAuroraReportProvider, auroraReports, errors)
	}

	@Provides
	@Reusable
	@Named(LAST_NAME)
	fun provideLastAuroraReportProvider(@Named(LAST_NAME) cache: SingletonCache<AuroraReport>): AuroraReportProvider {
		// TODO Replace with real implementation
		return object : AuroraReportProvider {
			override fun get(): AuroraReport {
				return cache.value
			}
		}
	}
}
