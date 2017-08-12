package se.gustavkarlsson.skylight.android.dagger.modules.clean

import dagger.Module
import dagger.Provides
import dagger.Reusable
import org.threeten.bp.Clock
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.actions.PresentRecentAuroraReport
import se.gustavkarlsson.skylight.android.actions_impl.aurora_reports.ProvideRecentAuroraReportToPublisher
import se.gustavkarlsson.skylight.android.dagger.BACKGROUND_UPDATE_TIMEOUT_NAME
import se.gustavkarlsson.skylight.android.dagger.LAST_NAME
import se.gustavkarlsson.skylight.android.dagger.NEW_NAME
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.extensions.now
import se.gustavkarlsson.skylight.android.services.providers.AuroraReportProvider
import se.gustavkarlsson.skylight.android.services.streams.StreamPublisher
import se.gustavkarlsson.skylight.android.util.UserFriendlyException
import javax.inject.Named

@Module(includes = arrayOf(
	LastAuroraReportProviderModule::class,
	NewAuroraReportProviderModule::class
))
class PresentRecentAuroraReportModule {

	@Provides
	@Reusable
	fun providePresentRecentAuroraReport(
		@Named(LAST_NAME) lastAuroraReportProvider: AuroraReportProvider,
		@Named(NEW_NAME) newAuroraReportProvider: AuroraReportProvider,
		clock: Clock,
		@Named(BACKGROUND_UPDATE_TIMEOUT_NAME) timeout: Duration,
		auroraReports: StreamPublisher<AuroraReport>,
		errors: StreamPublisher<UserFriendlyException>
	): PresentRecentAuroraReport {
		return ProvideRecentAuroraReportToPublisher(lastAuroraReportProvider, newAuroraReportProvider, clock::now, timeout, auroraReports, errors)
	}
}
