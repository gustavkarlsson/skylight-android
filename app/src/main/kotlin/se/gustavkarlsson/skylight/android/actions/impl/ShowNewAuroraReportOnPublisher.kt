package se.gustavkarlsson.skylight.android.actions.impl

import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.actions.ShowNewAuroraReport
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.AuroraReportProvider
import se.gustavkarlsson.skylight.android.services.StreamPublisher
import se.gustavkarlsson.skylight.android.util.UserFriendlyException

class ShowNewAuroraReportOnPublisher(
	private val newAuroraReportProvider: AuroraReportProvider,
	private val auroraReports: StreamPublisher<AuroraReport>,
	private val errors: StreamPublisher<UserFriendlyException>
) : ShowNewAuroraReport {
	override fun invoke() {
		try {
			val newAuroraReport = newAuroraReportProvider.get()
			auroraReports.publish(newAuroraReport)
		} catch(e: UserFriendlyException) {
			errors.publish(e)
		} catch(e: Exception) {
			errors.publish(UserFriendlyException(R.string.error_unknown_update_error, e))
		}
	}
}
