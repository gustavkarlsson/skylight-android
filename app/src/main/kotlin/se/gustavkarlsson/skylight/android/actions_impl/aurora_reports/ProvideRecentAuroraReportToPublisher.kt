package se.gustavkarlsson.skylight.android.actions_impl.aurora_reports

import org.threeten.bp.Duration
import org.threeten.bp.temporal.Temporal
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.actions.ShowRecentAuroraReport
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.extensions.until
import se.gustavkarlsson.skylight.android.services.Provider
import se.gustavkarlsson.skylight.android.services.streams.StreamPublisher
import se.gustavkarlsson.skylight.android.util.UserFriendlyException

class ProvideRecentAuroraReportToPublisher(
	private val lastAuroraReportProvider: Provider<AuroraReport>,
	private val newAuroraReportProvider: Provider<AuroraReport>,
	private val currentTime: () -> Temporal,
	private val maxAge: Duration,
	private val auroraReports: StreamPublisher<AuroraReport>,
	private val errors: StreamPublisher<UserFriendlyException>
) : ShowRecentAuroraReport {

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
			errors.publish(UserFriendlyException(R.string.error_unknown_update_error, e))
		}
	}

	private val AuroraReport.age: Duration
		get() = this.timestamp until currentTime()
}
