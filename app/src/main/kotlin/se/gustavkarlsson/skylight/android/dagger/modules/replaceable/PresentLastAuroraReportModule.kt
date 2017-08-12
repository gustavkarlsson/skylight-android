package se.gustavkarlsson.skylight.android.dagger.modules.replaceable

import dagger.Module
import dagger.Provides
import dagger.Reusable
import se.gustavkarlsson.skylight.android.actions.PresentLastAuroraReport
import se.gustavkarlsson.skylight.android.actions_impl.aurora_reports.ProvideLastAuroraReportToPublisher
import se.gustavkarlsson.skylight.android.dagger.LAST_NAME
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.providers.AuroraReportProvider
import se.gustavkarlsson.skylight.android.services.streams.StreamPublisher
import se.gustavkarlsson.skylight.android.util.UserFriendlyException
import javax.inject.Named

@Module
class PresentLastAuroraReportModule {

	@Provides
	@Reusable
	fun providePresentLastAuroraReport(@Named(LAST_NAME) lastAuroraReportProvider: AuroraReportProvider, auroraReports: StreamPublisher<AuroraReport>, errors: StreamPublisher<UserFriendlyException>): PresentLastAuroraReport {
		return ProvideLastAuroraReportToPublisher(lastAuroraReportProvider, auroraReports, errors)
	}
}
