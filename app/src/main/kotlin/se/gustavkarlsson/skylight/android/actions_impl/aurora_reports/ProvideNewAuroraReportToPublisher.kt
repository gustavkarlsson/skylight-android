package se.gustavkarlsson.skylight.android.actions_impl.aurora_reports

import dagger.Reusable
import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.actions.PresentNewAuroraReport
import se.gustavkarlsson.skylight.android.dagger.NEW_NAME
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.providers.AuroraReportProvider
import se.gustavkarlsson.skylight.android.services.streams.StreamPublisher
import se.gustavkarlsson.skylight.android.util.UserFriendlyException
import javax.inject.Inject
import javax.inject.Named

@Reusable
class ProvideNewAuroraReportToPublisher
@Inject
constructor(
        @Named(NEW_NAME) private val provider: AuroraReportProvider,
        private val publisher: StreamPublisher<AuroraReport>,
        private val errorPublisher: StreamPublisher<UserFriendlyException>
) : PresentNewAuroraReport {

    override fun invoke() {
        try {
            val newAuroraReport = provider.get()
            publisher.publish(newAuroraReport)
        } catch (e: UserFriendlyException) {
            errorPublisher.publish(e)
        } catch (e: Exception) {
            errorPublisher.publish(UserFriendlyException(R.string.error_unknown_update_error, e)) // TODO change error message
        }
    }
}
