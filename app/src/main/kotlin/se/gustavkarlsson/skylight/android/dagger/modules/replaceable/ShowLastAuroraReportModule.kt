package se.gustavkarlsson.skylight.android.dagger.modules.replaceable

import dagger.Module
import dagger.Provides
import dagger.Reusable
import org.threeten.bp.Clock
import se.gustavkarlsson.skylight.android.actions.ShowLastAuroraReport
import se.gustavkarlsson.skylight.android.actions.impl.ShowLastAuroraReportOnPublisher
import se.gustavkarlsson.skylight.android.cache.SingletonCache
import se.gustavkarlsson.skylight.android.dagger.LATEST_NAME
import se.gustavkarlsson.skylight.android.entities.*
import se.gustavkarlsson.skylight.android.extensions.now
import se.gustavkarlsson.skylight.android.services.LastAuroraReportProvider
import se.gustavkarlsson.skylight.android.services.StreamPublisher
import se.gustavkarlsson.skylight.android.util.UserFriendlyException
import javax.inject.Named

@Module
class ShowLastAuroraReportModule {

	@Provides
	@Reusable
	fun provideShowLastAuroraReport(lastAuroraReportProvider: LastAuroraReportProvider, @Named(LATEST_NAME) auroraReports: StreamPublisher<AuroraReport>, errors: StreamPublisher<UserFriendlyException>): ShowLastAuroraReport {
		return ShowLastAuroraReportOnPublisher(lastAuroraReportProvider, auroraReports, errors)
	}

	@Provides
	@Reusable
	fun provideLastAuroraReportProvider(@Named(LATEST_NAME) cache: SingletonCache<AuroraReport>, clock: Clock): LastAuroraReportProvider {
		// TODO Replace with real implementation
		return object : LastAuroraReportProvider {
			override fun get(): AuroraReport {
				var report = cache.value
				if (report == null) {
					val factors = AuroraFactors(
						GeomagActivity(null),
						GeomagLocation(null),
						Darkness(null),
						Visibility(null)
					)
					report = AuroraReport(clock.now, null, factors)
				}
				return report
			}
		}
	}
}
