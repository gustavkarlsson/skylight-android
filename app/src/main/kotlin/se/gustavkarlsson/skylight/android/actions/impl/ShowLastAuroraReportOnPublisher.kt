package se.gustavkarlsson.skylight.android.actions.impl

import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.actions.ShowLastAuroraReport
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.LastAuroraReportProvider
import se.gustavkarlsson.skylight.android.services.StreamPublisher
import se.gustavkarlsson.skylight.android.util.UserFriendlyException

class ShowLastAuroraReportOnPublisher(
	private val lastAuroraReportProvider: LastAuroraReportProvider,
	private val auroraReports: StreamPublisher<AuroraReport>,
	private val errors: StreamPublisher<UserFriendlyException>
) : ShowLastAuroraReport {
	override fun invoke() {
		try {
			val lastAuroraReport = lastAuroraReportProvider.get()
			auroraReports.publish(lastAuroraReport)
		} catch(e: UserFriendlyException) {
			errors.publish(e)
		} catch(e: Exception) {
			errors.publish(UserFriendlyException(R.string.error_unknown_update_error, e))
		}
	}
}
