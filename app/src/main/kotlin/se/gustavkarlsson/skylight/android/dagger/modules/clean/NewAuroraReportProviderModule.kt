package se.gustavkarlsson.skylight.android.dagger.modules.clean

import dagger.Module
import dagger.Provides
import dagger.Reusable
import se.gustavkarlsson.skylight.android.services.providers.AuroraReportProvider
import se.gustavkarlsson.skylight.android.dagger.NEW_NAME
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import javax.inject.Named

@Module
class NewAuroraReportProviderModule {

	@Provides
	@Reusable
	@Named(NEW_NAME)
	fun provideNewAuroraReportProvider(
		provider: AuroraReportProvider
	): AuroraReportProvider {
		// TODO Replace with real implementation
		return object : AuroraReportProvider {
			override fun get(): AuroraReport {
				return provider.get()
			}
		}
	}
}
