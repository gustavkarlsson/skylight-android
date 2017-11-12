package se.gustavkarlsson.skylight.android.actions_impl.aurora_reports

import dagger.Reusable
import io.reactivex.functions.Consumer
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.actions.GetNewAuroraReport
import se.gustavkarlsson.skylight.android.dagger.NEW_NAME
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.providers.AuroraReportProvider
import se.gustavkarlsson.skylight.android.util.UserFriendlyException
import javax.inject.Inject
import javax.inject.Named

@Reusable
class GetNewAuroraReportUsingRx
@Inject
constructor(
	@Named(NEW_NAME) private val provider: AuroraReportProvider,
	private val auroraReports: Consumer<AuroraReport>,
	private val errors: Consumer<UserFriendlyException>
) : GetNewAuroraReport {

    override fun invoke() {
		try {
			val auroraReport = provider.get().blockingGet()
			auroraReports.accept(auroraReport)
		} catch (e: Exception) {
			if (e is UserFriendlyException) {
				errors.accept(e)
			} else {
				val error = UserFriendlyException(R.string.error_unknown_update_error, e)
				errors.accept(error)
			}
		}
    }
}
