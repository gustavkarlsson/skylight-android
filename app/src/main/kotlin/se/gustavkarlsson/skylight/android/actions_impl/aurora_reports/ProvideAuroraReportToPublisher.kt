package se.gustavkarlsson.skylight.android.actions_impl.aurora_reports

import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.services.providers.AuroraReportProvider
import se.gustavkarlsson.skylight.android.entities.AuroraReport
import se.gustavkarlsson.skylight.android.services.streams.StreamPublisher
import se.gustavkarlsson.skylight.android.util.UserFriendlyException

abstract class ProvideAuroraReportToPublisher(
	private val provider: AuroraReportProvider,
	private val publisher: StreamPublisher<AuroraReport>,
	private val errorPublisher: StreamPublisher<UserFriendlyException>
) : () -> Unit {
	override fun invoke() {
		try {
			val value = provider.get()
			publisher.publish(value)
		} catch(e: UserFriendlyException) {
			errorPublisher.publish(e)
		} catch(e: Exception) {
			errorPublisher.publish(UserFriendlyException(R.string.error_unknown_update_error, e))
		}
	}
}
