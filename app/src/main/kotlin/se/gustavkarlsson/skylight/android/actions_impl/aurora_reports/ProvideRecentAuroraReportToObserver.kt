package se.gustavkarlsson.skylight.android.actions_impl.aurora_reports

import dagger.Reusable
import io.reactivex.Observer
import org.threeten.bp.Clock
import org.threeten.bp.Duration
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.actions.PresentRecentAuroraReport
import se.gustavkarlsson.skylight.android.dagger.LAST_NAME
import se.gustavkarlsson.skylight.android.dagger.NEW_NAME
import se.gustavkarlsson.skylight.android.dagger.REPORT_LIFETIME_NAME
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.extensions.now
import se.gustavkarlsson.skylight.android.extensions.until
import se.gustavkarlsson.skylight.android.services.providers.AuroraReportProvider
import se.gustavkarlsson.skylight.android.util.UserFriendlyException
import javax.inject.Inject
import javax.inject.Named

@Reusable
class ProvideRecentAuroraReportToObserver
@Inject
constructor(
	@Named(LAST_NAME) private val lastAuroraReportProvider: AuroraReportProvider,
	@Named(NEW_NAME) private val newAuroraReportProvider: AuroraReportProvider,
	private val auroraReports: Observer<AuroraReport>,
	private val errors: Observer<UserFriendlyException>,
	@Named(REPORT_LIFETIME_NAME) private val maxAge: Duration,
	private val clock: Clock
) : PresentRecentAuroraReport {
	init {
	    require(!maxAge.isNegative) { "maxAge may not be negative. Was: $maxAge" }
	}

	override fun invoke() {
		try {
			val lastAuroraReport = lastAuroraReportProvider.get()
			if (lastAuroraReport.age < maxAge) {
				auroraReports.onNext(lastAuroraReport)
			} else {
				val newAuroraReport = newAuroraReportProvider.get()
				auroraReports.onNext(newAuroraReport)
			}
		} catch(e: UserFriendlyException) {
			errors.onNext(e)
		} catch(e: Exception) {
			errors.onNext(UserFriendlyException(R.string.error_unknown_update_error, e))
		}
	}

	private val AuroraReport.age: Duration
		get() = this.timestamp until clock.now
}
