package se.gustavkarlsson.skylight.android.actions.impl

import se.gustavkarlsson.skylight.android.R
import se.gustavkarlsson.skylight.android.services.Provider
import se.gustavkarlsson.skylight.android.services.StreamPublisher
import se.gustavkarlsson.skylight.android.util.UserFriendlyException

abstract class ProvideToPublisher<T>(
	private val provider: Provider<T>,
	private val publisher: StreamPublisher<T>,
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
