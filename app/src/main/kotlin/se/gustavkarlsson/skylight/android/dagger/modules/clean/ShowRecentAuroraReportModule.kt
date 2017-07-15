package se.gustavkarlsson.skylight.android.dagger.modules.clean

import dagger.Module
import dagger.Provides
import dagger.Reusable
import org.threeten.bp.Clock
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.actions.ShowRecentAuroraReport
import se.gustavkarlsson.skylight.android.actions.impl.ProvideRecentAuroraReportToPublisher
import se.gustavkarlsson.skylight.android.dagger.BACKGROUND_UPDATE_TIMEOUT_NAME
import se.gustavkarlsson.skylight.android.dagger.LAST_NAME
import se.gustavkarlsson.skylight.android.dagger.NEW_NAME
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.extensions.now
import se.gustavkarlsson.skylight.android.services.Provider
import se.gustavkarlsson.skylight.android.services.StreamPublisher
import se.gustavkarlsson.skylight.android.util.UserFriendlyException
import javax.inject.Named

@Module(includes = arrayOf(
	LastAuroraReportProviderModule::class,
	NewAuroraReportProviderModule::class
))
class ShowRecentAuroraReportModule {

	@Provides
	@Reusable
	fun provideShowRecentAuroraReport(
		@Named(LAST_NAME) lastAuroraReportProvider: Provider<AuroraReport>,
		@Named(NEW_NAME) newAuroraReportProvider: Provider<AuroraReport>,
		clock: Clock,
		@Named(BACKGROUND_UPDATE_TIMEOUT_NAME) timeout: Duration,
		auroraReports: StreamPublisher<AuroraReport>,
		errors: StreamPublisher<UserFriendlyException>
	): ShowRecentAuroraReport {
		return ProvideRecentAuroraReportToPublisher(lastAuroraReportProvider, newAuroraReportProvider, clock::now, timeout, auroraReports, errors)
	}
}
