package se.gustavkarlsson.skylight.android.dagger.modules.replaceable

import dagger.Module
import dagger.Provides
import dagger.Reusable
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.actions.ShowNewAuroraReport
import se.gustavkarlsson.skylight.android.actions.impl.ShowNewAuroraReportOnPublisher
import se.gustavkarlsson.skylight.android.background.providers.AuroraReportProvider
import se.gustavkarlsson.skylight.android.dagger.BACKGROUND_UPDATE_TIMEOUT_NAME
import se.gustavkarlsson.skylight.android.dagger.LATEST_NAME
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.NewAuroraReportProvider
import se.gustavkarlsson.skylight.android.services.StreamPublisher
import se.gustavkarlsson.skylight.android.util.UserFriendlyException
import javax.inject.Named

@Module
class ShowNewAuroraReportModule {

	@Provides
	@Reusable
	fun provideShowNewAuroraReport(newAuroraReportProvider: NewAuroraReportProvider, @Named(LATEST_NAME) auroraReports: StreamPublisher<AuroraReport>, errors: StreamPublisher<UserFriendlyException>): ShowNewAuroraReport {
		return ShowNewAuroraReportOnPublisher(newAuroraReportProvider, auroraReports, errors)
	}

	@Provides
	@Reusable
	fun provideNewAuroraReportProvider(provider: AuroraReportProvider, @Named(BACKGROUND_UPDATE_TIMEOUT_NAME) timeout: Duration): NewAuroraReportProvider {
		// TODO Replace with real implementation
		return object : NewAuroraReportProvider {
			override fun get(): AuroraReport {
				return provider.getReport(timeout)
			}

		}
	}
}
