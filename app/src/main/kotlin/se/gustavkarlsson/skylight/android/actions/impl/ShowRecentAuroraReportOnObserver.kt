package se.gustavkarlsson.skylight.android.actions.impl

import io.reactivex.Observer
import org.threeten.bp.Duration
import org.threeten.bp.temporal.Temporal
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.actions.ShowRecentAuroraReport
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.extensions.until
import se.gustavkarlsson.skylight.android.services.GetLastAuroraReport
import se.gustavkarlsson.skylight.android.services.GetNewAuroraReport
import se.gustavkarlsson.skylight.android.util.UserFriendlyException

class ShowRecentAuroraReportOnObserver(
	private val getLastAuroraReport: GetLastAuroraReport,
	private val getNewAuroraReport: GetNewAuroraReport,
	private val currentTime: () -> Temporal,
	private val maxAge: Duration,
	private val auroraReports: Observer<AuroraReport>,
	private val errors: Observer<UserFriendlyException>
) : ShowRecentAuroraReport {

	override fun invoke() {
		try {
			val lastAuroraReport = getLastAuroraReport()
			if (lastAuroraReport.age < maxAge) {
				auroraReports.onNext(lastAuroraReport)
			} else {
				val newAuroraReport = getNewAuroraReport()
				auroraReports.onNext(newAuroraReport)
			}
		} catch(e: UserFriendlyException) {
			errors.onNext(e)
		} catch(e: Exception) {
			errors.onNext(UserFriendlyException(R.string.error_unknown_update_error, e))
		}
	}

	private val AuroraReport.age: Duration
		get() = this.timestamp until currentTime()
}
