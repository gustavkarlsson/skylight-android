package se.gustavkarlsson.skylight.android.dagger.modules.replaceable

import dagger.Module
import dagger.Provides
import dagger.Reusable
import se.gustavkarlsson.skylight.android.actions.ShowNewAuroraReport
import se.gustavkarlsson.skylight.android.actions_impl.aurora_reports.ProvideNewAuroraReportToPublisher
import se.gustavkarlsson.skylight.android.services.providers.AuroraReportProvider
import se.gustavkarlsson.skylight.android.dagger.NEW_NAME
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.streams.StreamPublisher
import se.gustavkarlsson.skylight.android.util.UserFriendlyException
import javax.inject.Named

@Module
class ShowNewAuroraReportModule {

	@Provides
	@Reusable
	fun provideShowNewAuroraReport(@Named(NEW_NAME) newAuroraReportProvider: AuroraReportProvider, auroraReports: StreamPublisher<AuroraReport>, errors: StreamPublisher<UserFriendlyException>): ShowNewAuroraReport {
		return ProvideNewAuroraReportToPublisher(newAuroraReportProvider, auroraReports, errors)
	}
}
