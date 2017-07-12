package se.gustavkarlsson.skylight.android.dagger.modules.replaceable

import dagger.Module
import dagger.Provides
import dagger.Reusable
import io.reactivex.Observer
import org.threeten.bp.Clock
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.actions.ShowLastAuroraReport
import se.gustavkarlsson.skylight.android.actions.impl.ShowLastAuroraReportOnObserver
import se.gustavkarlsson.skylight.android.cache.SingletonCache
import se.gustavkarlsson.skylight.android.dagger.BACKGROUND_UPDATE_TIMEOUT_NAME
import se.gustavkarlsson.skylight.android.dagger.LATEST_NAME
import se.gustavkarlsson.skylight.android.entities.*
import se.gustavkarlsson.skylight.android.extensions.now
import se.gustavkarlsson.skylight.android.services.GetLastAuroraReport
import se.gustavkarlsson.skylight.android.util.UserFriendlyException
import javax.inject.Named

@Module
class ShowLastAuroraReportModule {

	@Provides
	@Reusable
	fun provideShowLastAuroraReport(getLastAuroraReport: GetLastAuroraReport, @Named(LATEST_NAME) auroraReports: Observer<AuroraReport>, errors: Observer<UserFriendlyException>): ShowLastAuroraReport {
		return ShowLastAuroraReportOnObserver(getLastAuroraReport, auroraReports, errors)
	}

	@Provides
	@Reusable
	fun provideGetLastAuroraReport(@Named(LATEST_NAME) cache: SingletonCache<AuroraReport>, @Named(BACKGROUND_UPDATE_TIMEOUT_NAME) timeout: Duration, clock: Clock): GetLastAuroraReport {
		// TODO Replace with real implementation
		return object : GetLastAuroraReport {
			override fun invoke(): AuroraReport {
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
