package se.gustavkarlsson.skylight.android.actions_impl.aurora_reports

import dagger.Reusable
import org.threeten.bp.Clock
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.actions.PresentRecentAuroraReport
import se.gustavkarlsson.skylight.android.dagger.FOREGROUND_REPORT_LIFETIME_NAME
import se.gustavkarlsson.skylight.android.dagger.LAST_NAME
import se.gustavkarlsson.skylight.android.dagger.NEW_NAME
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.extensions.now
import se.gustavkarlsson.skylight.android.extensions.until
import se.gustavkarlsson.skylight.android.services.providers.AuroraReportProvider
import se.gustavkarlsson.skylight.android.services.streams.StreamPublisher
import se.gustavkarlsson.skylight.android.util.UserFriendlyException
import javax.inject.Inject
import javax.inject.Named

@Reusable
class ProvideRecentAuroraReportToPublisher
@Inject
constructor(
        @Named(LAST_NAME) private val lastAuroraReportProvider: AuroraReportProvider,
        @Named(NEW_NAME) private val newAuroraReportProvider: AuroraReportProvider,
        private val auroraReports: StreamPublisher<AuroraReport>,
        private val errors: StreamPublisher<UserFriendlyException>,
        @Named(FOREGROUND_REPORT_LIFETIME_NAME) private val maxAge: Duration,
        private val clock: Clock
) : PresentRecentAuroraReport {
	init {
	    require(!maxAge.isNegative) { "maxAge may not be negative. Was: $maxAge" }
	}

	override fun invoke() {
		try {
			val lastAuroraReport = lastAuroraReportProvider.get()
			if (lastAuroraReport.age < maxAge) {
				auroraReports.publish(lastAuroraReport)
			} else {
				val newAuroraReport = newAuroraReportProvider.get()
				auroraReports.publish(newAuroraReport)
			}
		} catch(e: UserFriendlyException) {
			errors.publish(e)
		} catch(e: Exception) {
			errors.publish(UserFriendlyException(R.string.error_unknown_update_error, e)) // TODO change error message
		}
	}

	private val AuroraReport.age: Duration
		get() = this.timestamp until clock.now
}
