package se.gustavkarlsson.skylight.android.actions.impl

import org.threeten.bp.Duration
import org.threeten.bp.temporal.Temporal
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.actions.ShowRecentAuroraReport
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.extensions.until
import se.gustavkarlsson.skylight.android.services.AuroraReportProvider
import se.gustavkarlsson.skylight.android.services.StreamPublisher
import se.gustavkarlsson.skylight.android.util.UserFriendlyException

class ShowRecentAuroraReportOnObserver(
	private val lastAuroraReportProvider: AuroraReportProvider,
	private val newAuroraReportProvider: AuroraReportProvider,
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
