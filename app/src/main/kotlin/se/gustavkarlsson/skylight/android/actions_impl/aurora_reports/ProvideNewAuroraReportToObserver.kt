package se.gustavkarlsson.skylight.android.actions_impl.aurora_reports

import dagger.Reusable
import io.reactivex.Observer
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.actions.PresentNewAuroraReport
import se.gustavkarlsson.skylight.android.dagger.NEW_NAME
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.providers.AuroraReportProvider
import se.gustavkarlsson.skylight.android.util.UserFriendlyException
import javax.inject.Inject
import javax.inject.Named

@Reusable
class ProvideNewAuroraReportToObserver
@Inject
constructor(
	@Named(NEW_NAME) private val provider: AuroraReportProvider,
	private val auroraReportObserver: Observer<AuroraReport>,
	private val errorObserver: Observer<UserFriendlyException>
) : PresentNewAuroraReport {

    override fun invoke() {
        try {
            val newAuroraReport = provider.get()
            auroraReportObserver.onNext(newAuroraReport)
        } catch (e: UserFriendlyException) {
            errorObserver.onNext(e)
        } catch (e: Exception) {
            errorObserver.onNext(UserFriendlyException(R.string.error_unknown_update_error, e))
        }
    }
}
