package se.gustavkarlsson.skylight.android.dagger.modules.replaceable

import dagger.Module
import dagger.Provides
import dagger.Reusable
import io.reactivex.Observer
import org.threeten.bp.Clock
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.actions.ShowRecentAuroraReport
import se.gustavkarlsson.skylight.android.actions.impl.ShowRecentAuroraReportOnObserver
import se.gustavkarlsson.skylight.android.dagger.BACKGROUND_UPDATE_TIMEOUT_NAME
import se.gustavkarlsson.skylight.android.dagger.LATEST_NAME
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.extensions.now
import se.gustavkarlsson.skylight.android.services.GetLastAuroraReport
import se.gustavkarlsson.skylight.android.services.GetNewAuroraReport
import se.gustavkarlsson.skylight.android.util.UserFriendlyException
import javax.inject.Named

@Module
class ShowRecentAuroraReportModule {

	@Provides
	@Reusable
	fun provideShowRecentAuroraReport(getLastAuroraReport: GetLastAuroraReport, getNewAuroraReport: GetNewAuroraReport, clock: Clock, @Named(BACKGROUND_UPDATE_TIMEOUT_NAME) timeout: Duration, @Named(LATEST_NAME) auroraReports: Observer<AuroraReport>, errors: Observer<UserFriendlyException>): ShowRecentAuroraReport {
		return ShowRecentAuroraReportOnObserver(getLastAuroraReport, getNewAuroraReport, clock::now, timeout, auroraReports, errors)
	}
}
