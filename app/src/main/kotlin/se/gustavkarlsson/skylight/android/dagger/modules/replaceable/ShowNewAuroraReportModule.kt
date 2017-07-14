package se.gustavkarlsson.skylight.android.dagger.modules.replaceable

import dagger.Module
import dagger.Provides
import dagger.Reusable
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.actions.ShowNewAuroraReport
import se.gustavkarlsson.skylight.android.actions.impl.ProvideToPublisher
import se.gustavkarlsson.skylight.android.background.providers.AuroraReportProvider
import se.gustavkarlsson.skylight.android.dagger.BACKGROUND_UPDATE_TIMEOUT_NAME
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
		return object : ShowNewAuroraReport {
			private val provideToPublisher = ProvideToPublisher(newAuroraReportProvider, auroraReports, errors)
			override fun invoke() {
				provideToPublisher()
			}
		}
	}

	@Provides
	@Reusable
	@Named(NEW_NAME)
	fun provideLastAuroraReportProvider(provider: AuroraReportProvider, @Named(BACKGROUND_UPDATE_TIMEOUT_NAME) timeout: Duration): Provider<AuroraReport> {
		// TODO Replace with real implementation
		return object : Provider<AuroraReport> {
			override fun get(): AuroraReport {
				return provider.getReport(timeout)
			}

		}
	}
}
