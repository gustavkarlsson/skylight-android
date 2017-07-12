package se.gustavkarlsson.skylight.android.actions.impl

import io.reactivex.Observer
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.actions.ShowNewAuroraReport
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.GetNewAuroraReport
import se.gustavkarlsson.skylight.android.util.UserFriendlyException

class ShowNewAuroraReportOnObserver(
	private val getNewAuroraReport: GetNewAuroraReport,
	private val auroraReports: Observer<AuroraReport>,
	private val errors: Observer<UserFriendlyException>
) : ShowNewAuroraReport {
	override fun invoke() {
		try {
			val newAuroraReport = getNewAuroraReport()
			auroraReports.onNext(newAuroraReport)
		} catch(e: UserFriendlyException) {
			errors.onNext(e)
		} catch(e: Exception) {
			errors.onNext(UserFriendlyException(R.string.error_unknown_update_error, e))
		}
	}
}
