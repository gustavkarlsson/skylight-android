package se.gustavkarlsson.skylight.android.dagger.modules.replaceable

import dagger.Module
import dagger.Provides
import dagger.Reusable
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.actions.ShowNewAuroraReport
import se.gustavkarlsson.skylight.android.actions.impl.ShowNewAuroraReportOnPublisher
import se.gustavkarlsson.skylight.android.dagger.BACKGROUND_UPDATE_TIMEOUT_NAME
import se.gustavkarlsson.skylight.android.dagger.NEW_NAME
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.AuroraReportProvider
import se.gustavkarlsson.skylight.android.services.StreamPublisher
import se.gustavkarlsson.skylight.android.util.UserFriendlyException
import javax.inject.Named

@Module
class ShowNewAuroraReportModule {

	@Provides
	@Reusable
	fun provideShowNewAuroraReport(@Named(NEW_NAME) newAuroraReportProvider: AuroraReportProvider, auroraReports: StreamPublisher<AuroraReport>, errors: StreamPublisher<UserFriendlyException>): ShowNewAuroraReport {
		return ShowNewAuroraReportOnPublisher(newAuroraReportProvider, auroraReports, errors)
	}

	@Provides
	@Reusable
	@Named(NEW_NAME)
	fun provideLastAuroraReportProvider(provider: se.gustavkarlsson.skylight.android.background.providers.AuroraReportProvider, @Named(BACKGROUND_UPDATE_TIMEOUT_NAME) timeout: Duration): AuroraReportProvider {
		// TODO Replace with real implementation
		return object : se.gustavkarlsson.skylight.android.services.AuroraReportProvider {
			override fun get(): AuroraReport {
				return provider.getReport(timeout)
			}

		}
	}
}
