package se.gustavkarlsson.skylight.android.actions_impl.aurora_reports

import dagger.Reusable
import io.reactivex.Completable
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
) : GetNewAuroraReport { // TODO Move error handling to callers?

	override fun invoke(): Completable {
		return provider.get()
			.doOnSuccess {
				propagateValue(it)
			}
			.doOnError {
				propagateError(it)
			}
			.toCompletable()
	}

	private fun propagateValue(it: AuroraReport) {
		auroraReports.accept(it)
	}

	private fun propagateError(error: Throwable) {
		if (error is UserFriendlyException) {
			errors.accept(error)
		} else {
			val wrapped = UserFriendlyException(R.string.error_unknown_update_error, error)
			errors.accept(wrapped)
		}
	}
}
