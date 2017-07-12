package se.gustavkarlsson.skylight.android.actions.impl

import io.reactivex.Observer
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.actions.ShowLastAuroraReport
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.GetLastAuroraReport
import se.gustavkarlsson.skylight.android.util.UserFriendlyException

class ShowLastAuroraReportOnObserver(
	private val getLastAuroraReport: GetLastAuroraReport,
	private val auroraReports: Observer<AuroraReport>,
	private val errors: Observer<UserFriendlyException>
) : ShowLastAuroraReport {
	override fun invoke() {
		try {
			val lastAuroraReport = getLastAuroraReport()
			auroraReports.onNext(lastAuroraReport)
		} catch(e: UserFriendlyException) {
			errors.onNext(e)
		} catch(e: Exception) {
			errors.onNext(UserFriendlyException(R.string.error_unknown_update_error, e))
		}
	}
}
