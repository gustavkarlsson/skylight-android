package se.gustavkarlsson.skylight.android.dagger.modules.replaceable

import dagger.Module
import dagger.Provides
import dagger.Reusable
import se.gustavkarlsson.skylight.android.actions.ShowNewAuroraReport
import se.gustavkarlsson.skylight.android.actions.impl.ProvideNewAuroraReportToPublisher
import se.gustavkarlsson.skylight.android.dagger.NEW_NAME
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.Provider
import se.gustavkarlsson.skylight.android.services.StreamPublisher
import se.gustavkarlsson.skylight.android.util.UserFriendlyException
import javax.inject.Named

@Module
class ShowNewAuroraReportModule {

	@Provides
	@Reusable
	fun provideShowNewAuroraReport(@Named(NEW_NAME) newAuroraReportProvider: Provider<AuroraReport>, auroraReports: StreamPublisher<AuroraReport>, errors: StreamPublisher<UserFriendlyException>): ShowNewAuroraReport {
		return ProvideNewAuroraReportToPublisher(newAuroraReportProvider, auroraReports, errors)
	}
}
