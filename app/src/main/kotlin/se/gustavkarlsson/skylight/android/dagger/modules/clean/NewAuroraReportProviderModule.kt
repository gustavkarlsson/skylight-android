package se.gustavkarlsson.skylight.android.dagger.modules.clean

import dagger.Module
import dagger.Provides
import dagger.Reusable
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.background.providers.AuroraReportProvider
import se.gustavkarlsson.skylight.android.dagger.BACKGROUND_UPDATE_TIMEOUT_NAME
import se.gustavkarlsson.skylight.android.dagger.NEW_NAME
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.Provider
import javax.inject.Named

@Module
class NewAuroraReportProviderModule {

	@Provides
	@Reusable
	@Named(NEW_NAME)
	fun provideNewAuroraReportProvider(
		provider: AuroraReportProvider,
		@Named(BACKGROUND_UPDATE_TIMEOUT_NAME) timeout: Duration
	): Provider<AuroraReport> {
		// TODO Replace with real implementation
		return object : Provider<AuroraReport> {
			override fun get(): AuroraReport {
				return provider.getReport(timeout)
			}

		}
	}
}
