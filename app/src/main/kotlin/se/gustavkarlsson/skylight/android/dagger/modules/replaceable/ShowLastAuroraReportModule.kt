package se.gustavkarlsson.skylight.android.dagger.modules.replaceable

import dagger.Module
import dagger.Provides
import dagger.Reusable
import se.gustavkarlsson.skylight.android.actions.ShowLastAuroraReport
import se.gustavkarlsson.skylight.android.actions.impl.ProvideToPublisher
import se.gustavkarlsson.skylight.android.dagger.LAST_NAME
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.Provider
import se.gustavkarlsson.skylight.android.services.SingletonCache
import se.gustavkarlsson.skylight.android.services.StreamPublisher
import se.gustavkarlsson.skylight.android.util.UserFriendlyException
import javax.inject.Named

@Module
class ShowLastAuroraReportModule {

	@Provides
	@Reusable
	fun provideShowLastAuroraReport(@Named(LAST_NAME) lastAuroraReportProvider: Provider<AuroraReport>, auroraReports: StreamPublisher<AuroraReport>, errors: StreamPublisher<UserFriendlyException>): ShowLastAuroraReport {
		return object : ShowLastAuroraReport {
			private val provideToPublisher = ProvideToPublisher(lastAuroraReportProvider, auroraReports, errors)
			override fun invoke() {
				provideToPublisher()
			}
		}
	}

	@Provides
	@Reusable
	@Named(LAST_NAME)
	fun provideLastAuroraReportProvider(@Named(LAST_NAME) cache: SingletonCache<AuroraReport>): Provider<AuroraReport> {
		// TODO Replace with real implementation
		return object : Provider<AuroraReport> {
			override fun get(): AuroraReport {
				return cache.value
			}
		}
	}
}
