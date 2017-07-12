package se.gustavkarlsson.skylight.android.dagger.modules.replaceable

import dagger.Module
import dagger.Provides
import dagger.Reusable
import io.reactivex.Observer
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.actions.ShowNewAuroraReport
import se.gustavkarlsson.skylight.android.actions.impl.ShowNewAuroraReportOnObserver
import se.gustavkarlsson.skylight.android.background.providers.AuroraReportProvider
import se.gustavkarlsson.skylight.android.dagger.BACKGROUND_UPDATE_TIMEOUT_NAME
import se.gustavkarlsson.skylight.android.dagger.LATEST_NAME
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.GetNewAuroraReport
import se.gustavkarlsson.skylight.android.util.UserFriendlyException
import javax.inject.Named

@Module
class ShowNewAuroraReportModule {

	@Provides
	@Reusable
	fun provideShowNewAuroraReport(getNewAuroraReport: GetNewAuroraReport, @Named(LATEST_NAME) auroraReports: Observer<AuroraReport>, errors: Observer<UserFriendlyException>): ShowNewAuroraReport {
		return ShowNewAuroraReportOnObserver(getNewAuroraReport, auroraReports, errors)
	}

	@Provides
	@Reusable
	fun provideGetNewAuroraReport(provider: AuroraReportProvider, @Named(BACKGROUND_UPDATE_TIMEOUT_NAME) timeout: Duration): GetNewAuroraReport {
		// TODO Replace with real implementation
		return object : GetNewAuroraReport {
			override fun invoke(): AuroraReport {
				return provider.getReport(timeout)
			}

		}
	}
}
